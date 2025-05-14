import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { INotificationAttachment } from '../notification-attachment.model';
import {
  sampleWithFullData,
  sampleWithNewData,
  sampleWithPartialData,
  sampleWithRequiredData,
} from '../notification-attachment.test-samples';

import { NotificationAttachmentService, RestNotificationAttachment } from './notification-attachment.service';

const requireRestSample: RestNotificationAttachment = {
  ...sampleWithRequiredData,
  createdAt: sampleWithRequiredData.createdAt?.toJSON(),
};

describe('NotificationAttachment Service', () => {
  let service: NotificationAttachmentService;
  let httpMock: HttpTestingController;
  let expectedResult: INotificationAttachment | INotificationAttachment[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(NotificationAttachmentService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find('9fec3727-3421-4967-b213-ba36557ca194').subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a NotificationAttachment', () => {
      const notificationAttachment = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(notificationAttachment).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a NotificationAttachment', () => {
      const notificationAttachment = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(notificationAttachment).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a NotificationAttachment', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of NotificationAttachment', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a NotificationAttachment', () => {
      const expected = true;

      service.delete('9fec3727-3421-4967-b213-ba36557ca194').subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    it('should handle exceptions for searching a NotificationAttachment', () => {
      const queryObject: any = {
        page: 0,
        size: 20,
        query: '',
        sort: [],
      };
      service.search(queryObject).subscribe(() => expectedResult);

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(null, { status: 500, statusText: 'Internal Server Error' });
      expect(expectedResult).toBe(null);
    });

    describe('addNotificationAttachmentToCollectionIfMissing', () => {
      it('should add a NotificationAttachment to an empty array', () => {
        const notificationAttachment: INotificationAttachment = sampleWithRequiredData;
        expectedResult = service.addNotificationAttachmentToCollectionIfMissing([], notificationAttachment);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(notificationAttachment);
      });

      it('should not add a NotificationAttachment to an array that contains it', () => {
        const notificationAttachment: INotificationAttachment = sampleWithRequiredData;
        const notificationAttachmentCollection: INotificationAttachment[] = [
          {
            ...notificationAttachment,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addNotificationAttachmentToCollectionIfMissing(notificationAttachmentCollection, notificationAttachment);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a NotificationAttachment to an array that doesn't contain it", () => {
        const notificationAttachment: INotificationAttachment = sampleWithRequiredData;
        const notificationAttachmentCollection: INotificationAttachment[] = [sampleWithPartialData];
        expectedResult = service.addNotificationAttachmentToCollectionIfMissing(notificationAttachmentCollection, notificationAttachment);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(notificationAttachment);
      });

      it('should add only unique NotificationAttachment to an array', () => {
        const notificationAttachmentArray: INotificationAttachment[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const notificationAttachmentCollection: INotificationAttachment[] = [sampleWithRequiredData];
        expectedResult = service.addNotificationAttachmentToCollectionIfMissing(
          notificationAttachmentCollection,
          ...notificationAttachmentArray,
        );
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const notificationAttachment: INotificationAttachment = sampleWithRequiredData;
        const notificationAttachment2: INotificationAttachment = sampleWithPartialData;
        expectedResult = service.addNotificationAttachmentToCollectionIfMissing([], notificationAttachment, notificationAttachment2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(notificationAttachment);
        expect(expectedResult).toContain(notificationAttachment2);
      });

      it('should accept null and undefined values', () => {
        const notificationAttachment: INotificationAttachment = sampleWithRequiredData;
        expectedResult = service.addNotificationAttachmentToCollectionIfMissing([], null, notificationAttachment, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(notificationAttachment);
      });

      it('should return initial array if no NotificationAttachment is added', () => {
        const notificationAttachmentCollection: INotificationAttachment[] = [sampleWithRequiredData];
        expectedResult = service.addNotificationAttachmentToCollectionIfMissing(notificationAttachmentCollection, undefined, null);
        expect(expectedResult).toEqual(notificationAttachmentCollection);
      });
    });

    describe('compareNotificationAttachment', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareNotificationAttachment(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: '48dceab7-3a17-4181-86ca-670bfc1a30cb' };
        const entity2 = null;

        const compareResult1 = service.compareNotificationAttachment(entity1, entity2);
        const compareResult2 = service.compareNotificationAttachment(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: '48dceab7-3a17-4181-86ca-670bfc1a30cb' };
        const entity2 = { id: '149ab936-c806-4b24-802c-cab259b50e63' };

        const compareResult1 = service.compareNotificationAttachment(entity1, entity2);
        const compareResult2 = service.compareNotificationAttachment(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: '48dceab7-3a17-4181-86ca-670bfc1a30cb' };
        const entity2 = { id: '48dceab7-3a17-4181-86ca-670bfc1a30cb' };

        const compareResult1 = service.compareNotificationAttachment(entity1, entity2);
        const compareResult2 = service.compareNotificationAttachment(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
