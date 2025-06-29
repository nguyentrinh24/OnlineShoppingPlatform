import { Inject, Injectable } from '@angular/core';
import { CommonModule, DOCUMENT } from '@angular/common';
import { JwtHelperService } from '@auth0/angular-jwt';

@Injectable({
    providedIn: 'root',
})
export class TokenService {
    private readonly TOKEN_KEY = 'access-token';

    private jwtHelperService = new JwtHelperService();
    localStorage?: Storage;

    constructor(@Inject(DOCUMENT) private document: Document) {
        this.localStorage = document.defaultView?.localStorage;
    }

    getToken(): string | null {
        return this.localStorage?.getItem(this.TOKEN_KEY) ?? null;
    }

    setToken(token: string): void {
        this.localStorage?.setItem(this.TOKEN_KEY, token);
    }

    removeToken(): void {
        this.localStorage?.removeItem(this.TOKEN_KEY);
    }

    getUserId(): number {
        const token = this.getToken();
        if (!token) return 0;
        const userObject = this.jwtHelperService.decodeToken(token);
        return 'userId' in userObject ? parseInt(userObject['userId']) : 0;
    }

    isTokenExpired(): boolean {
        const token = this.getToken();
        return token ? this.jwtHelperService.isTokenExpired(token) : false;
    }
}
