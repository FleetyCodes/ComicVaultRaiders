import { Component, signal, ViewChild, ElementRef, effect, DestroyRef, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ChatService } from '../../services/chat.service';


@Component({
  selector: 'app-chat-widget',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './chat-widget.component.html',
  styleUrl: './chat-widget.component.scss',
})
export class ChatWidgetComponent {
  isExpanded = signal(false);
  inputText = '';
  private destroyRef = inject(DestroyRef);
  private robotImages = ['robot_0.png', 'robot_1.png', 'robot_2.png', 'robot_3.png'];
  currentBotIcon = signal(this.robotImages[0]);
  private currentIndex = 0;

  @ViewChild('messagesContainer') messagesContainer!: ElementRef<HTMLDivElement>;

  constructor(public chatService: ChatService) {
    // auto-scroll to bottom when new messages arrive
    effect(() => {
      this.chatService.messages();
      queueMicrotask(() => this.scrollToBottom());
    });

    const intervalId = setInterval(() => {
      this.currentIndex = (this.currentIndex + 1) % this.robotImages.length;
      this.currentBotIcon.set(this.robotImages[this.currentIndex]);
    }, 1500); // 1.5 másodpercenként

    this.destroyRef.onDestroy(() => clearInterval(intervalId));
  }

  toggleExpand() {
    this.isExpanded.update(v => !v);
  }

  onEnter(event: Event) {
    event.preventDefault();
    const text = this.inputText.trim();
    if (!text) return;
    this.chatService.sendMessage(text);
    this.inputText = '';
  }

  private scrollToBottom() {
    const el = this.messagesContainer?.nativeElement;
    if (el) el.scrollTop = el.scrollHeight;
  }
}