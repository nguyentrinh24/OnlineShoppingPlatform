package com.project.shopapp.services.Product;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.shopapp.dtos.ProductDTO;
import com.project.shopapp.dtos.ProductImageDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.exceptions.InvalidParamException;
import com.project.shopapp.models.Category;
import com.project.shopapp.models.Product;
import com.project.shopapp.models.ProductImage;
import com.project.shopapp.repositories.CategoryRepository;
import com.project.shopapp.repositories.ProductImageRepository;
import com.project.shopapp.repositories.ProductRepository;
import com.project.shopapp.responses.Product.ProductResponse;
import com.project.shopapp.services.Redis.BaseRedis;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductImageRepository productImageRepository;
    private final BaseRedis baseRedis;// Dùng để cache object ProductResponse theo id
    private final ProductRedisService productRedisService;// Dùng để cache list ProductResponse theo filter + paging
    private final ObjectMapper objectMapper;

    @Value("${spring.data.redis.use-redis-cache}")
    private boolean useRedisCache;


    @Override
    @Transactional
    public Product createProduct(ProductDTO productDTO) throws DataNotFoundException {
        Category existingCategory = categoryRepository
                .findById(productDTO.getCategoryId())
                .orElseThrow(() ->
                        new DataNotFoundException(
                                "Cannot find category with id: "+productDTO.getCategoryId()));

        Product newProduct = Product.builder()
                .name(productDTO.getName())
                .price(productDTO.getPrice())
                .thumbnail(productDTO.getThumbnail())
                .quantity(productDTO.getQuantity())
                .stock_quantity(productDTO.getStock_quantity())
                .description(productDTO.getDescription())
                .category(existingCategory)
                .build();

        //delete cache list
        if(useRedisCache)
        {
            productRedisService.clear();
        }
        return productRepository.save(newProduct);
    }

    @Override
    public Product getProductById(long productId) throws Exception {
        String cacheKey = "product:" + productId;
        // lấy từ Redis
        if (useRedisCache) {
            Product cachedProduct = baseRedis.get(cacheKey);
            if (cachedProduct != null) {
                return cachedProduct;
            }
        }

        // không có trong cache truy vấn database
        Product product = productRepository.getDetailProduct(productId)
                .orElseThrow(() -> new DataNotFoundException(
                        "Không tìm thấy Product với id = " + productId));

        // Lưu vào cache để lần sau lấy nhanh
        if (useRedisCache) {
            baseRedis.set(cacheKey, product);
        }

        return product;
    }

    //  TÌM NHIỀU SẢN PHẨM THEO DANH SÁCH ID
    @Override
    public List<Product> findProductsByIds(List<Long> productIds) {
        return productRepository.findProductsByIds(productIds);
    }


    @Override
    public Page<ProductResponse> getAllProducts(String keyword,
                                                Long categoryId, PageRequest pageRequest) {
        //  lấy từ cache
        if (useRedisCache) {
            try {
                List<ProductResponse> cachedList =
                        productRedisService.getAllProducts(keyword, categoryId, pageRequest);
                if (cachedList != null) {
                    // Chuyển List thành Page
                    return new PageImpl<>(cachedList, pageRequest, cachedList.size());
                }
            } catch (JsonProcessingException ignored) {
            }
        }

        // Nếu cache không có, truy vấn DB và map về DTO
        Page<Product> productsPage =
                productRepository.searchProducts(categoryId, keyword, pageRequest);
        Page<ProductResponse> responsePage =
                productsPage.map(ProductResponse::fromProduct);

        // Lưu kết quả vào cache
        if (useRedisCache) {
            try {
                productRedisService.saveAllProducts(
                        responsePage.getContent(), keyword, categoryId, pageRequest);
            } catch (JsonProcessingException ignored) {
            }
        }

        return responsePage;
    }
    @Override
    @Transactional
    public Product updateProduct(long id, ProductDTO productDTO) throws Exception {
        // Kiểm tra product tồn tại
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(
                        "Không tìm thấy Product với id = " + id));

        // Kiểm tra category tồn tại
        Category category = categoryRepository.findById(productDTO.getCategoryId())
                .orElseThrow(() -> new DataNotFoundException(
                        "Không tìm thấy Category với id = " + productDTO.getCategoryId()));

        // Cập nhật
        existingProduct.setName(productDTO.getName());
        existingProduct.setCategory(category);
        existingProduct.setPrice(productDTO.getPrice());
        existingProduct.setStock_quantity(productDTO.getStock_quantity());
        existingProduct.setQuantity(productDTO.getQuantity());
        existingProduct.setDescription(productDTO.getDescription());
        if (productDTO.getThumbnail() != null && !productDTO.getThumbnail().isEmpty()) {
            existingProduct.setThumbnail(productDTO.getThumbnail());
        }
        // Lưu thay đổi
        Product savedProduct = productRepository.save(existingProduct);

        //Xóa cache để dữ liệu mới
        if (useRedisCache) {
            baseRedis.delete("product:" + id);
            productRedisService.clear();
        }
        return savedProduct;
    }

    @Override
    @Transactional
    public void deleteProduct(long id) {
        Optional<Product> optional = productRepository.findById(id);
        if (optional.isPresent()) {
            productRepository.delete(optional.get());
            if (useRedisCache) {
                baseRedis.delete("product:" + id);
                productRedisService.clear();
            }
        }
    }

    @Override
    public boolean existsByName(String name) {

        return productRepository.existsByName(name);
    }

    // ====== QUẢN LÝ ẢNH SẢN PHẨM ======
    @Override
    @Transactional
    public ProductImage createProductImage(
            Long productId,
            ProductImageDTO productImageDTO) throws Exception {
        Product existingProduct = productRepository
                .findById(productId)
                .orElseThrow(() ->
                        new DataNotFoundException(
                                "Cannot find product with id: "+productImageDTO.getProductId()));
        ProductImage newProductImage = ProductImage.builder()
                .product(existingProduct)
                .imageUrl(productImageDTO.getImageUrl())
                .build();
        //Ko cho insert quá 5 ảnh cho 1 sản phẩm
        int size = productImageRepository.findByProductId(productId).size();
        if(size >= ProductImage.MAXIMUM_IMAGES_PER_PRODUCT) {
            throw new InvalidParamException(
                    "Number of images must be <= "
                    +ProductImage.MAXIMUM_IMAGES_PER_PRODUCT);
        }
        return productImageRepository.save(newProductImage);
    }


    @Override
    public void deleteProductImage(Long imageId) throws Exception {
        ProductImage image = productImageRepository.findById(imageId)
                .orElseThrow(() -> new DataNotFoundException("Image not found with id = " + imageId));

        Path imagePath = Paths.get("uploads/" + image.getImageUrl());
        if (Files.exists(imagePath)) {
            Files.delete(imagePath);
        }

        productImageRepository.deleteById(imageId);
    }

}
