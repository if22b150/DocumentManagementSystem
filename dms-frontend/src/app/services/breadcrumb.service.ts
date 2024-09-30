import {computed, Injectable, signal} from '@angular/core';
import {BreadcrumbModel} from "../models/breadcrumb.model";

@Injectable({
  providedIn: 'root'
})
export class BreadcrumbService {
  _breadcrumbs = signal<BreadcrumbModel[]>([])
  public breadcrumbs = computed(() => this._breadcrumbs())

  public reset() {
    this._breadcrumbs.set([])
  }

  public add(bc: BreadcrumbModel) {
    this._breadcrumbs.update(bcs => {
      return [...bcs, {text: bc.text, path: bc.path, loading: bc.loading}]
    })
  }

  public updateCurrentConcreteModel(nameOrTitle: string) {
    this._breadcrumbs.update(bcs => {
      return bcs.map((bc, index) =>
        index === bcs.length - 1 ? { ...bc, text: nameOrTitle } : bc
      );
    });
  }

  public setLoadedBreadcrumb(model: any, resource: string) {
    this._breadcrumbs.update(bcs => {
      return bcs.map(bc => (bc.text == resource && bc.loading) ? {
        text: model.name ?? model.title,
        path: bc.path,
        loading: false
      } : bc)
    })
  }

  constructor() { }
}
