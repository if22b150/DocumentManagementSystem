import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {TranslateModule, TranslateService} from "@ngx-translate/core";
import {BreadcrumbsComponent} from "./components/utils/breadcrumbs/breadcrumbs.component";

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, BreadcrumbsComponent, TranslateModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent {
  title = 'dms-frontend';

  constructor(public translate: TranslateService) {
    translate.addLangs(['en']);
    translate.setDefaultLang('en');

    // const browserLang = translate.getBrowserLang();
    // translate.use(browserLang!.match(/en|de/) ? browserLang! : 'en');
    translate.use('en')
  }
}
