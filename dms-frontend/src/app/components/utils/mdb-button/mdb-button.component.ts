import {Component, input, output} from '@angular/core';
import {MdbRippleModule} from "mdb-angular-ui-kit/ripple";
import {NgIf, NgStyle} from "@angular/common";
import {RouterLink} from "@angular/router";
import {TranslateModule} from "@ngx-translate/core";

@Component({
  selector: 'app-mdb-button',
  standalone: true,
  imports: [MdbRippleModule, NgIf, RouterLink, TranslateModule, NgStyle],
  templateUrl: './mdb-button.component.html',
  styleUrl: './mdb-button.component.scss',
})
export class MdbButtonComponent {
  text = input<string | undefined>();
  iconClass = input<string | undefined>();
  iconPosRight = input<boolean | undefined>(false);
  color = input<string>('primary');
  size = input<string | undefined>();
  // rippleColor = input<string|undefined>("light")
  outlined = input<boolean | undefined>(false);
  floating = input<boolean | undefined>(false);
  customClasses = input('');
  routerLink = input<string | undefined>();
  loading = input<boolean | undefined>(false);
  isSubmit = input<boolean | undefined>(false);
  styles = input<{ [klass: string]: any }>({});

  onClick = output();
}
