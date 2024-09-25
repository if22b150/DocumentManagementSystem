import { Injectable } from '@angular/core';
import {MdbNotificationService} from "mdb-angular-ui-kit/notification";
import {TranslateService} from "@ngx-translate/core";
import {ToastComponent} from "../components/utils/toast/toast.component";

@Injectable({
  providedIn: 'root',
})
export class CustomNotificationService {
  private notify(message: string, type: string, autohide: boolean = true, delay: number = 2500): void {
    // setTimeout is workaround for stacking bug
    setTimeout(()  => {
      this.notificationService.open(ToastComponent, { stacking: true, data: { text: this.translate.instant(message), type: type }, autohide: autohide, delay: delay })
    })
  }

  success(message: string, autohide: boolean = true, delay: number = 2500): void {
    this.notify(message, 'success', autohide, delay)
  }

  error(message: string, autohide: boolean = true, delay: number = 2500): void {
    this.notify(message, 'danger', autohide, delay)
  }

  constructor(private notificationService: MdbNotificationService,
              private translate: TranslateService) { }
}
