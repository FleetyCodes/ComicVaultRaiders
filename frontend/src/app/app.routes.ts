import { Routes } from '@angular/router';
import { RegisterComponent } from './screens/registration-page/registration.component';
import { LandingPageComponent } from './screens/landing-page/landing-page.component';
import { LoginComponent } from './screens/login-page/login.component';
import { LoggedInPageComponent } from './screens/logged-in-page/logged-in';
import { NoAuthGuard } from './no.auth.guard';
import { AuthGuard } from './auth.guard';

export const routes: Routes = [
  { path: '', component: LandingPageComponent, canActivate: [NoAuthGuard] },
  { path: 'register', component: RegisterComponent, canActivate: [NoAuthGuard] },
  { path: 'login', component: LoginComponent, canActivate: [NoAuthGuard] }, 
  { path: 'logged-in', component: LoggedInPageComponent, canActivate: [AuthGuard] }, 
  //{ path: '**', redirectTo: '', pathMatch: 'full' }   // fallback
];