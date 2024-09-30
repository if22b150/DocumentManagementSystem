import { Component } from '@angular/core';
import {MdbNotificationRef} from "mdb-angular-ui-kit/notification";

@Component({
  selector: 'app-toast',
  standalone: true,
  imports: [],
  templateUrl: './toast.component.html',
  styleUrl: './toast.component.scss'
})
export class ToastComponent {
  text: string | null = null;
  type: string = 'primary';

  constructor(public notificationRef: MdbNotificationRef<ToastComponent>) {}
}
