import { Injectable } from '@angular/core';
import {
  ActivatedRouteSnapshot,
  RouterStateSnapshot,
  CanActivateFn
} from '@angular/router';
import { TokenService } from '../services/token.service';
import { Router } from '@angular/router'; // Đảm bảo bạn đã import Router ở đây.
import { inject } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard {
  constructor(
    private tokenService: TokenService,
    private router: Router,
  ) { }

  canActivate(next: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
    const isTokenExpired = this.tokenService.isTokenExpired();
    const isUserIdValid = this.tokenService.getUserId() > 0;
    //debugger
    if (!isTokenExpired && isUserIdValid) {
      return true;
    } else {

      this.router.navigate(['/login']);
      return false;
    }
  }
}

// Sử dụng functional guard như sau:
export const AuthGuardFn: CanActivateFn = (next: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean => {
  //debugger
  return inject(AuthGuard).canActivate(next, state);
}
