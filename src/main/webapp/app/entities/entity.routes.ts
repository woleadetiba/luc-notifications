import { Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'dictionary',
    data: { pageTitle: 'lucNotificationsApp.lucNotificationsDictionary.home.title' },
    loadChildren: () => import('./LUCNotifications/dictionary/dictionary.routes'),
  },
  {
    path: 'notification',
    data: { pageTitle: 'lucNotificationsApp.lucNotificationsNotification.home.title' },
    loadChildren: () => import('./LUCNotifications/notification/notification.routes'),
  },
  {
    path: 'notification-attachment',
    data: { pageTitle: 'lucNotificationsApp.lucNotificationsNotificationAttachment.home.title' },
    loadChildren: () => import('./LUCNotifications/notification-attachment/notification-attachment.routes'),
  },
  /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
];

export default routes;
