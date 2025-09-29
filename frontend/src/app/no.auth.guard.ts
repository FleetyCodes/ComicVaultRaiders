import { Injectable } from "@angular/core";
import { CanActivate, Router, UrlTree } from "@angular/router";
import { UserService } from "./services/user.service";

@Injectable({ providedIn: 'root' })
export class NoAuthGuard implements CanActivate {
  constructor(private auth: UserService, private router: Router) {
    
   }

  canActivate(): boolean | UrlTree {
    if (this.auth.getToken()==null || this.auth.getToken()==='' ) {
      return true;
    } else {
      return this.router.parseUrl('/logged-in');
    }
  }

}
