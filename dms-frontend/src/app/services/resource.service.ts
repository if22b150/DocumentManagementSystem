import { computed, Injectable, signal } from '@angular/core';
import {
  finalize,
  Observable,
  Subject,
  Subscription,
  takeUntil,
  tap,
} from 'rxjs';
import { HttpClient } from '@angular/common/http';
import {AModel} from "../models/a-model.model";
import {TableLoadingModel} from "../models/table-column.model";
import {BreadcrumbService} from "./breadcrumb.service";
import {RequestOptions} from "../models/request-options.model";
import {environment} from "../environments/environment";
import {CustomNotificationService} from "./custom-notification.service";

@Injectable({
  providedIn: 'root',
})
export abstract class ResourceService<M extends AModel> {
  // currently fetched models, from index routes, probably paginated
  apiResponse = signal<{ data: M[]; filters?: string }>({ data: [] });

  models = computed(() => this.apiResponse().data);

  loading = signal<TableLoadingModel>({ loading: false });

  // concrete fetched models that are needed for detail pages or breadcrumbs
  usedConcreteModels = signal<M[]>([]);
  currentConcreteModel = signal<M | null>(null);

  private getAllSubscription: Subscription | null = null;
  private cancelRequest$ = new Subject<void>();

  removeModel(id: number) {
    // Update the concrete models
    this.usedConcreteModels.update((models) => {
      return models.filter((m) => m.id != id);
    });

    // Check if the model exists in the apiResponse.data array and update it if necessary
    if (this.models() && this.models().length > 0) {
      this.apiResponse.update((apiResp) => {
        const updatedData = apiResp.data.filter((m) => m.id != id);
        return { ...apiResp, data: updatedData };
      });
    }
  }

  addConcreteModel(model: M) {
    this.usedConcreteModels.update((models) => {
      return [...models, model];
    });
    this.currentConcreteModel.set(model);
  }

  updateModel(model: M) {
    // Update the concrete models
    this.usedConcreteModels.update((models) => {
      return models.map((m) => (m.id == model.id ? model : m));
    });
    this.currentConcreteModel.set(model);

    // Check if the model exists in the apiResponse.data array and update it if necessary
    if (this.models() && this.models().length > 0) {
      this.apiResponse.update((apiResp) => {
        const updatedData = apiResp.data.map((m) =>
          m.id === model.id ? model : m,
        );
        return { ...apiResp, data: updatedData };
      });
    }
  }

  protected constructor(
    protected http: HttpClient,
    protected breadcrumbService: BreadcrumbService,
    protected resourceUrl: string,
    protected notificationService: CustomNotificationService
  ) {}

  getAll(options?: RequestOptions): void {
    // Cancel previous request if it exists
    if (this.getAllSubscription) {
      this.cancelRequest$.next(); // Signal to cancel the request
    }

    this.loading.set({
      loading: true,
      hasFilters: options?.urlParams != undefined,
      lazyLoad: options?.lazyLoad,
    });

    let params = options?.urlParams ? '?' + options?.urlParams : '';

    const request$ = this.http.get<any>(
      environment.apiUrl + this.resourceUrl + params,
    );

    this.getAllSubscription = request$
      .pipe(
        takeUntil(this.cancelRequest$), // Cancel request when triggered
        finalize(() => this.loading.set({ loading: false })),
      )
      .subscribe({
        next: (response: M[]) => {
          this.apiResponse.set({ data: response, filters: options?.urlParams });
        },
        error: (e) => {
          console.error(e)
          this.notificationService.error('ERROR_OCCURRED');
        },
      });
  }

  getOne(id: number): Observable<M> {
    this.currentConcreteModel.set(null);
    return this.http
      .get<M>(environment.apiUrl + this.resourceUrl + `/${id}`)
      .pipe(
        tap({
          next: (m: M) => {
            if (m) this.addConcreteModel(m);
          },
        }),
      );
  }

  update(id: number, data: any): Observable<M> {
    return this.http
      .put<M>(environment.apiUrl + this.resourceUrl + `/${id}`, data)
      .pipe(
        tap({
          next: (m: M) => {
            this.updateModel(m);
            this.breadcrumbService.updateCurrentConcreteModel(
              m.title!,
            );
          },
        }),
      );
  }

  delete(id: number): Observable<any> {
    return this.http
      .delete<any>(environment.apiUrl + this.resourceUrl + `/${id}`)
      .pipe(
        tap({
          next: () => {
            this.removeModel(id);
          },
        }),
      );
  }
}
