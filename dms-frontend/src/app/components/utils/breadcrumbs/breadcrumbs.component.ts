import {
  Component,
  EnvironmentInjector,
  inject, input,
  OnInit,
  runInInjectionContext,
} from '@angular/core';
import { NavigationEnd, Router, RouterLink } from '@angular/router';
import { AsyncPipe, NgForOf, NgIf } from '@angular/common';
import { TranslateModule } from '@ngx-translate/core';
import { filter, take } from 'rxjs';;
import { SpinnerComponent } from '../spinner/spinner.component';
import { toObservable } from '@angular/core/rxjs-interop';
import {CustomNotificationService} from "../../../services/custom-notification.service";
import {DocumentService} from "../../../services/document.service";
import {BreadcrumbService} from "../../../services/breadcrumb.service";
import {ResourceService} from "../../../services/resource.service";

@Component({
  selector: 'app-breadcrumbs',
  standalone: true,
  imports: [
    RouterLink,
    AsyncPipe,
    NgIf,
    NgForOf,
    TranslateModule,
    SpinnerComponent,
  ],
  templateUrl: './breadcrumbs.component.html',
  styleUrl: './breadcrumbs.component.scss',
})
export class BreadcrumbsComponent implements OnInit {
  routePaths: string[] = [];
  private environmentInjector = inject(EnvironmentInjector);
  baseParents: string[] = ['documents'];
  validParentPaths: string[] = ['documents'];
  constructor(
    private router: Router,
    private notificationService: CustomNotificationService,
    private documentService: DocumentService,
    public breadcrumbService: BreadcrumbService
  ) {}

  ngOnInit() {
    this.setRoutePaths();

    this.router.events
      .pipe(filter((event) => event instanceof NavigationEnd))
      .subscribe({
        next: () => {
          this.setRoutePaths();
        },
      });
  }

  setRoutePaths() {
    this.routePaths = this.router.url.split('/').filter((p) => p != '');
    this.initBreadcrumbs();
  }

  initBreadcrumbs() {
    let parents: {
      text: string;
      path: string;
      service: ResourceService<any>;
    }[] = [];
    let parent: {
      text: string;
      path: string;
      service: ResourceService<any>;
    } | null = null;
    let currentPath = '';

    this.breadcrumbService.reset();

    this.routePaths.forEach((path) => {
      let text = '';
      let loading = false;
      let newParent;
      let isSubParent = false;
      if ((newParent = this.isNotConcreteModel(path))) {
        parent = newParent;

        isSubParent = this.isSubParent(parents);
        parents.push(newParent);

        text = `NAVIGATION.${newParent.text}`;
        newParent.path = currentPath + `/${newParent.path}`;
      } else {
        // get concrete model
        if (parent) {
          let id = +path;
          if (isNaN(id)) {
            this.handleInvalidConcreteModel(parent);
            return;
          }
          let concreteModel = this.getConcreteModel(id, parent);
          if (concreteModel) {
            text = concreteModel.name;
          } else {
            text = parent.text;
            loading = true;
          }
        } else {
          this.handleInvalidParentModel();
          return;
        }
      }

      currentPath += `/${path}`;

      if (!isSubParent) {
        this.breadcrumbService.add({
          text: text,
          path: currentPath,
          loading: loading,
        });
      }
    });
  }

  isSubParent(parents: any[]) {
    let isSub = false;
    parents.forEach((p) => {
      if (this.baseParents.find((bp) => bp == p.text.toLowerCase()) != null)
        isSub = true;
    });
    return isSub;
  }

  isNotConcreteModel(path: string) {
    if (this.validParentPaths.indexOf(path) === -1) return null;

    // TODO: DASHBOARD NEEDS TO BE HANDLED SPECIFICALLY

    let service;
    switch (path) {
      case 'documents':
        service = this.documentService;
        break;
    }
    // if(path != "dashboard")
    //   service!.getAll()
    return {
      text: path.toUpperCase(),
      path: path,
      service: service as ResourceService<any>,
    };
  }

  getConcreteModel(
    id: number,
    parent: { text: string; path: string; service: ResourceService<any> },
  ) {
    let model = null;
    model =
      parent.service.models().find((m) => m.id == id) ??
      parent.service.usedConcreteModels().find((m) => m.id == id);

    if (!model) {
      if (parent.service.loading().loading) {
        runInInjectionContext(this.environmentInjector, () => {
          toObservable(parent.service.loading)
            .pipe(
              filter((v) => !v.loading),
              take(1),
            )
            .subscribe({
              next: (l) => {
                model =
                  parent.service.models().find((m) => m.id == id) ??
                  parent.service.usedConcreteModels().find((m) => m.id == id);
                if (model)
                  this.breadcrumbService.setLoadedBreadcrumb(
                    model,
                    parent.text,
                  );
                else this.fetchConcreteModel(id, parent);
              },
              error: () => {
                this.handleInvalidParentModel();
              },
            });
        });
      } else this.fetchConcreteModel(id, parent);
    }
    return model;
  }

  fetchConcreteModel(
    id: number,
    parent: { text: string; path: string; service: ResourceService<any> },
  ) {
    parent.service.getOne(id).subscribe({
      next: (model) => {
        this.breadcrumbService.setLoadedBreadcrumb(model, parent.text);
      },
      error: () => {
        this.handleInvalidConcreteModel(parent);
      },
    });
  }

  handleInvalidParentModel() {
    this.router
      .navigate(['/'])
      .then(() => this.notificationService.error('PAGE_NOT_FOUND'));
  }

  handleInvalidConcreteModel(parent: {
    text: string;
    path: string;
    service: ResourceService<any>;
  }) {
    this.router
      .navigate([parent.path])
      .then(() =>
        this.notificationService.error(
          `NAVIGATION.${parent.text}_RESOURCE_NOT_FOUND`,
        ),
      );
  }
}
