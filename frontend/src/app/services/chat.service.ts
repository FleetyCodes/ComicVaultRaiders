import { Injectable, signal } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { ChatMessage } from '../models/chat-message.model';
import { environment } from '../../environments/environment.dev';
import { UserService } from './user.service';


@Injectable({ providedIn: 'root' })
export class ChatService {
  messages = signal<ChatMessage[]>([]);
  isLoading = signal(false);

  private apiUrl = environment.apiUrl + "v1/ai-agent";

  constructor(private http: HttpClient, private userService: UserService) {}

  sendMessage(text: string) {
    const userMsg: ChatMessage = {
      id: crypto.randomUUID(),
      role: 'user',
      text,
      timestamp: new Date(),
    };
    this.messages.update(msgs => [...msgs, userMsg]);
    this.isLoading.set(true);

    const token = this.userService.getToken();
    const headers = new HttpHeaders({
        'Authorization': `Bearer ${token}`,
    });

    this.http.post<{ responseMessage: string }>(`${this.apiUrl}/chat`, { message: text }, { headers }).subscribe({
      next: (res) => {
        console.log('AI response:', res.responseMessage);
        this.messages.update(msgs => [...msgs, {
          id: crypto.randomUUID(),
          role: 'bot',
          text: res.responseMessage ,
          timestamp: new Date(),
        }]);
        this.isLoading.set(false);
      },
      error: () => {
        this.messages.update(msgs => [...msgs, {
          id: crypto.randomUUID(),
          role: 'bot',
          text: 'An error occurred, please try again later.',
          timestamp: new Date(),
        }]);
        this.isLoading.set(false);
      }
    });
  }
}