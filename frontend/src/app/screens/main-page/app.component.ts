import { Component, OnInit, signal } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { HelloService } from '../../services/hello.service';
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
  providers: [AuthGuard, NoAuthGuard, Idle, Keepalive],
  styleUrls: ['./app.component.scss'],
  standalone: true,
  imports: [CommonModule, RouterOutlet, MatButtonModule],
})


export class AppComponent implements OnInit {

  constructor(public helloService: HelloService, private idleService: IdleService, public userService: UserService, private router: Router) { }

  
  title = 'Comic Vault Raiders';

  ngOnInit() {
    this.helloService.getHello().subscribe({
      //this.helloService.getAllComics().subscribe({
      next: (response) => {
        this.helloService.setHelloTestMessage(response);
      },
      error: (err) => {
        console.error('Error:', err);
        this.helloService.setHelloTestMessage('Could not load data, please try again later. ');
      }
    });
  }


  logout() {
    this.userService.logOut();
    this.userService.clearToken();
    this.idleService.stopIdleTimer();
    this.router.navigate(['/']);
  }

}

