import { Component, OnInit } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { HelloService } from './services/hello.service';


@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],  
  standalone: true,
  imports: [
    RouterOutlet
],
})


export class AppComponent implements OnInit {
  title = 'Comic Vault Raiders';
  helloMessage: string = 'Loading...';

  constructor(private helloService: HelloService) {}
  
  ngOnInit() {
     this.helloService.getHello().subscribe({
    //this.helloService.getAllComics().subscribe({
      next: (response) => {
        this.helloMessage = response;
      },
      error: (err) => {
        console.error('Error:', err);
        this.helloMessage = 'Could not load data, please try again later. ';
      }
    });
  }
}

