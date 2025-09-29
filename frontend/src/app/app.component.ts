import { Component, OnInit, signal } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { HelloService } from './services/hello.service';
import { NoAuthGuard } from './no.auth.guard';
import { AuthGuard } from './auth.guard';
import { CommonModule } from '@angular/common';


@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  providers: [AuthGuard, NoAuthGuard],
  styleUrls: ['./app.component.scss'],  
  standalone: true,
  imports: [CommonModule, RouterOutlet],
})


export class AppComponent implements OnInit {
  title = 'Comic Vault Raiders';

  constructor(public helloService: HelloService) {}
  
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
}

