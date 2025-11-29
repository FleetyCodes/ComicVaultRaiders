import { Component, OnInit, signal } from '@angular/core';
import { Router, RouterModule, RouterOutlet } from '@angular/router';
import { NoAuthGuard } from '../../no.auth.guard';
import { AuthGuard } from '../../auth.guard';
import { CommonModule } from '@angular/common';
import { Idle } from '@ng-idle/core';
import { Keepalive } from '@ng-idle/keepalive';
import { IdleService } from '../../services/idle.service';
import { MatButtonModule } from '@angular/material/button';
import { UserService } from '../../services/user.service';



@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  providers: [AuthGuard, NoAuthGuard, Idle, Keepalive, ],
  styleUrls: ['./app.component.scss'],
  standalone: true,
  imports: [CommonModule, RouterOutlet, MatButtonModule, RouterModule],
})


export class AppComponent implements OnInit {

  constructor(private idleService: IdleService, public userService: UserService, private router: Router) { }

  
  title = 'Comic Vault Raiders';
  

  protected isLeftSideNav = signal<boolean>(false); 
  

  ngOnInit() {
    this.isLeftSideNav.set(this.userService.isLeftSidedNavbar());
  }


  logout() {
    this.userService.logOut().subscribe();
    this.userService.clearToken();
    this.idleService.stopIdleTimer();
    this.router.navigate(['/']);
  }

  setLeftSideNav(isLeft: boolean){ 
    this.userService.setLeftSidedNavbar(isLeft);
    this.isLeftSideNav.set(isLeft);
  }

  public isMobile(): boolean {
    return /Android|iPhone|iPad|iPod/i.test(navigator.userAgent);
  }
}

