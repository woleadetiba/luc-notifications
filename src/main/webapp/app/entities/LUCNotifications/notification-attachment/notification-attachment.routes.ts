import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import NotificationAttachmentResolve from './route/notification-attachment-routing-resolve.service';

const notificationAttachmentRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/notification-attachment.component').then(m => m.NotificationAttachmentComponent),
    data: {},
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/notification-attachment-detail.component').then(m => m.NotificationAttachmentDetailComponent),
    resolve: {
      notificationAttachment: NotificationAttachmentResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/notification-attachment-update.component').then(m => m.NotificationAttachmentUpdateComponent),
    resolve: {
      notificationAttachment: NotificationAttachmentResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/notification-attachment-update.component').then(m => m.NotificationAttachmentUpdateComponent),
    resolve: {
      notificationAttachment: NotificationAttachmentResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default notificationAttachmentRoute;
