import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../dictionary.test-samples';

import { DictionaryFormService } from './dictionary-form.service';

describe('Dictionary Form Service', () => {
  let service: DictionaryFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(DictionaryFormService);
  });

  describe('Service methods', () => {
    describe('createDictionaryFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createDictionaryFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            keyName: expect.any(Object),
            keyCode: expect.any(Object),
            label: expect.any(Object),
            description: expect.any(Object),
          }),
        );
      });

      it('passing IDictionary should create a new form with FormGroup', () => {
        const formGroup = service.createDictionaryFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            keyName: expect.any(Object),
            keyCode: expect.any(Object),
            label: expect.any(Object),
            description: expect.any(Object),
          }),
        );
      });
    });

    describe('getDictionary', () => {
      it('should return NewDictionary for default Dictionary initial value', () => {
        const formGroup = service.createDictionaryFormGroup(sampleWithNewData);

        const dictionary = service.getDictionary(formGroup) as any;

        expect(dictionary).toMatchObject(sampleWithNewData);
      });

      it('should return NewDictionary for empty Dictionary initial value', () => {
        const formGroup = service.createDictionaryFormGroup();

        const dictionary = service.getDictionary(formGroup) as any;

        expect(dictionary).toMatchObject({});
      });

      it('should return IDictionary', () => {
        const formGroup = service.createDictionaryFormGroup(sampleWithRequiredData);

        const dictionary = service.getDictionary(formGroup) as any;

        expect(dictionary).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IDictionary should not enable id FormControl', () => {
        const formGroup = service.createDictionaryFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewDictionary should disable id FormControl', () => {
        const formGroup = service.createDictionaryFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
