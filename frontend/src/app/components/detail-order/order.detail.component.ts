import { Component, OnInit } from '@angular/core';
import { Product } from '../../models/product';
import { CartService } from '../../services/cart.service';
import { ProductService } from '../../services/product.service';
import { OrderService } from '../../services/order.service';
import { OrderDTO } from '../../dtos/order/order.dto';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { OrderResponse } from '../../responses/order/order.response';
import { environment } from '../../../environments/environment';
import { OrderDetail } from '../../models/order.detail';
import { FooterComponent } from '../footer/footer.component';
import { HeaderComponent } from '../header/header.component';
import { CommonModule } from '@angular/common';


@Component({
  selector: 'app-order-detail',
  templateUrl: './order.detail.component.html',
  styleUrls: ['./order.detail.component.scss'],
  standalone: true,
  imports: [
    FooterComponent,
    HeaderComponent,
    CommonModule,
    RouterModule
  ]
})
export class OrderDetailComponent implements OnInit {
  orderResponse: OrderResponse = {
    id: 0, // Hoặc bất kỳ giá trị số nào bạn muốn
    user_id: 0,
    fullname: '',
    phone_number: '',
    email: '',
    address: '',
    note: '',
    order_date: new Date(),
    status: '',
    total_money: 0, // Hoặc bất kỳ giá trị số nào bạn muốn
    shipping_method: '',
    shipping_address: '',
    shipping_date: new Date(),
    payment_method: '',
    order_details: [] // Một mảng rỗng
  };
  constructor(
    private orderService: OrderService,
    private route: ActivatedRoute
  ) { }

  ngOnInit(): void {
    this.getOrderDetails();

  }

  getOrderDetails(): void {
    debugger
    const orderId = Number(this.route.snapshot.paramMap.get('id'));
    this.orderService.getOrderById(orderId).subscribe({
      next: (response: any) => {
        // Gán lại cho latestOrderId tại đây
        debugger
        this.orderService.latestOrderId = response.id;

        this.orderResponse.id = response.id;
        this.orderResponse.user_id = response.user_id;
        this.orderResponse.fullname = response.fullname;
        this.orderResponse.email = response.email;
        this.orderResponse.phone_number = response.phone_number;
        this.orderResponse.address = response.address;
        this.orderResponse.note = response.note;
        this.orderResponse.order_date = new Date(
          response.order_date[0],
          response.order_date[1] - 1,
          response.order_date[2]
        );

        this.orderResponse.order_details = response.order_details.map((order_detail: OrderDetail) => {
          order_detail.product.thumbnail = `${environment.apiBaseUrl}/products/images/${order_detail.product.thumbnail}`;
          return order_detail;
        });

        this.orderResponse.payment_method = response.payment_method;
        this.orderResponse.shipping_date = new Date(
          response.shipping_date[0],
          response.shipping_date[1] - 1,
          response.shipping_date[2]
        );

        this.orderResponse.shipping_method = response.shipping_method;
        this.orderResponse.status = response.status;
        this.orderResponse.total_money = response.total_money;


      },
      complete: () => {
        //debugger;        
      },
      error: (error: any) => {
        //debugger;
        console.error('Error fetching detail:', error);
      }
    });
  }
}

