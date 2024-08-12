import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, UrlTree, Router } from '@angular/router';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthGuardService implements CanActivate {

  constructor(private router: Router) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {

    const expectedRole = route.data['expectedRole'];
    const token = localStorage.getItem('Token');
    const userRole = localStorage.getItem('role'); // assuming you store user role in local storage
    console.log(`AuthGuardService: expectedRole: ${expectedRole}, token: ${token}, userRole: ${userRole}`);

    if (!token || !userRole || (userRole !== 'trader' && userRole !== 'client')) {
      // if token or userRole is null, or userRole is not valid, redirect to login page
      return this.router.parseUrl('/login');
    } 

    if (state.url.includes('/client') && userRole !== 'client') {
      // If URL includes '/client' but userRole is not 'client', redirect to trader homepage
      console.log(`Redirecting to: /trader/homepage`);
      return this.router.parseUrl('/trader/homepage');
    } 

    if (state.url.includes('/trader') && userRole !== 'trader') {
      // If URL includes '/trader' but userRole is not 'trader', redirect to client homepage
      console.log(`Redirecting to: /client/homepage`);
      return this.router.parseUrl('/client/homepage');
    } 

    // if everything is fine, do not interrupt navigation
    return true;
  }

}
