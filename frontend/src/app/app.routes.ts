import { Routes } from '@angular/router';
import { RegisterComponent } from './screens/registration-page/registration.component';
import { AppComponent } from './app.component';
import { LandingPageComponent } from './screens/landing-page/landing-page.component';

export const routes: Routes = [
  { path: '', component: LandingPageComponent },      // root / landing page
  { path: 'register', component: RegisterComponent }, // /register page
  //{ path: '**', redirectTo: '', pathMatch: 'full' }   // fallback
];