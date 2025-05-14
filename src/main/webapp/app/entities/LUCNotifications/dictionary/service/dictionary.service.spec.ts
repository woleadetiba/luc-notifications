import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { IDictionary } from '../dictionary.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../dictionary.test-samples';

import { DictionaryService } from './dictionary.service';

const requireRestSample: IDictionary = {
  ...sampleWithRequiredData,
};

describe('Dictionary Service', () => {
  let service: DictionaryService;
  let httpMock: HttpTestingController;
  let expectedResult: IDictionary | IDictionary[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(DictionaryService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a Dictionary', () => {
      const dictionary = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(dictionary).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Dictionary', () => {
      const dictionary = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(dictionary).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Dictionary', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Dictionary', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Dictionary', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    it('should handle exceptions for searching a Dictionary', () => {
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

    describe('addDictionaryToCollectionIfMissing', () => {
      it('should add a Dictionary to an empty array', () => {
        const dictionary: IDictionary = sampleWithRequiredData;
        expectedResult = service.addDictionaryToCollectionIfMissing([], dictionary);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(dictionary);
      });

      it('should not add a Dictionary to an array that contains it', () => {
        const dictionary: IDictionary = sampleWithRequiredData;
        const dictionaryCollection: IDictionary[] = [
          {
            ...dictionary,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addDictionaryToCollectionIfMissing(dictionaryCollection, dictionary);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Dictionary to an array that doesn't contain it", () => {
        const dictionary: IDictionary = sampleWithRequiredData;
        const dictionaryCollection: IDictionary[] = [sampleWithPartialData];
        expectedResult = service.addDictionaryToCollectionIfMissing(dictionaryCollection, dictionary);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(dictionary);
      });

      it('should add only unique Dictionary to an array', () => {
        const dictionaryArray: IDictionary[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const dictionaryCollection: IDictionary[] = [sampleWithRequiredData];
        expectedResult = service.addDictionaryToCollectionIfMissing(dictionaryCollection, ...dictionaryArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const dictionary: IDictionary = sampleWithRequiredData;
        const dictionary2: IDictionary = sampleWithPartialData;
        expectedResult = service.addDictionaryToCollectionIfMissing([], dictionary, dictionary2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(dictionary);
        expect(expectedResult).toContain(dictionary2);
      });

      it('should accept null and undefined values', () => {
        const dictionary: IDictionary = sampleWithRequiredData;
        expectedResult = service.addDictionaryToCollectionIfMissing([], null, dictionary, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(dictionary);
      });

      it('should return initial array if no Dictionary is added', () => {
        const dictionaryCollection: IDictionary[] = [sampleWithRequiredData];
        expectedResult = service.addDictionaryToCollectionIfMissing(dictionaryCollection, undefined, null);
        expect(expectedResult).toEqual(dictionaryCollection);
      });
    });

    describe('compareDictionary', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareDictionary(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 14247 };
        const entity2 = null;

        const compareResult1 = service.compareDictionary(entity1, entity2);
        const compareResult2 = service.compareDictionary(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 14247 };
        const entity2 = { id: 2718 };

        const compareResult1 = service.compareDictionary(entity1, entity2);
        const compareResult2 = service.compareDictionary(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 14247 };
        const entity2 = { id: 14247 };

        const compareResult1 = service.compareDictionary(entity1, entity2);
        const compareResult2 = service.compareDictionary(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
