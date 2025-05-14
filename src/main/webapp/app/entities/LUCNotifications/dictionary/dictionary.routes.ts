import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import DictionaryResolve from './route/dictionary-routing-resolve.service';

const dictionaryRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/dictionary.component').then(m => m.DictionaryComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/dictionary-detail.component').then(m => m.DictionaryDetailComponent),
    resolve: {
      dictionary: DictionaryResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/dictionary-update.component').then(m => m.DictionaryUpdateComponent),
    resolve: {
      dictionary: DictionaryResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/dictionary-update.component').then(m => m.DictionaryUpdateComponent),
    resolve: {
      dictionary: DictionaryResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default dictionaryRoute;
