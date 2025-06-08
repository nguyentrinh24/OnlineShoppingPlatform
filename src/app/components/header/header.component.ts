import { Component, OnInit } from '@angular/core';
import { UserService } from '../../services/user.service';

import { ActivatedRoute, Router } from '@angular/router';
import { TokenService } from '../../services/token.service';
import { UserResponse } from '../../responses/user/user.response';

import { CommonModule } from '@angular/common';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { RouterModule } from '@angular/router';
import { OrderService } from '../../services/order.service';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss'],
  standalone: true,
  imports: [
    CommonModule,
    NgbModule,
    RouterModule
  ]
})
export class HeaderComponent implements OnInit {
  userResponse?: UserResponse | null;
  isPopoverOpen = false;
  activeNavItem: number = 0;


  constructor(
    private userService: UserService,
    private tokenService: TokenService,
    private router: Router,
    private orderService: OrderService
  ) {

  }
  ngOnInit() {
    this.userResponse = this.userService.getUserResponseFromLocalStorage();
  }

  togglePopover(event: Event): void {
    event.preventDefault();
    this.isPopoverOpen = !this.isPopoverOpen;
  }

  handleItemClick(index: number): void {
    if (index === 0) {
      this.router.navigate(['/user-profile']);
    } else if (index === 1) {
      const orderId = this.orderService.latestOrderId;
      if (orderId) {
        this.router.navigate(['/orders', orderId]);
      } else {
        console.error('Không có đơn hàng nào được lưu.');
      }
    } else if (index === 2) {
      this.userService.removeUserFromLocalStorage();
      this.tokenService.removeToken();
      this.userResponse = this.userService.getUserResponseFromLocalStorage();
    }
    this.isPopoverOpen = false;
  }



  setActiveNavItem(index: number) {
    this.activeNavItem = index;
    //alert(this.activeNavItem);
  }
}
