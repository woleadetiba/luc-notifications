import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { DictionaryService } from '../service/dictionary.service';
import { IDictionary } from '../dictionary.model';
import { DictionaryFormService } from './dictionary-form.service';

import { DictionaryUpdateComponent } from './dictionary-update.component';

describe('Dictionary Management Update Component', () => {
  let comp: DictionaryUpdateComponent;
  let fixture: ComponentFixture<DictionaryUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let dictionaryFormService: DictionaryFormService;
  let dictionaryService: DictionaryService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [DictionaryUpdateComponent],
      providers: [
        provideHttpClient(),
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(DictionaryUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(DictionaryUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    dictionaryFormService = TestBed.inject(DictionaryFormService);
    dictionaryService = TestBed.inject(DictionaryService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should update editForm', () => {
      const dictionary: IDictionary = { id: 2718 };

      activatedRoute.data = of({ dictionary });
      comp.ngOnInit();

      expect(comp.dictionary).toEqual(dictionary);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDictionary>>();
      const dictionary = { id: 14247 };
      jest.spyOn(dictionaryFormService, 'getDictionary').mockReturnValue(dictionary);
      jest.spyOn(dictionaryService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ dictionary });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: dictionary }));
      saveSubject.complete();

      // THEN
      expect(dictionaryFormService.getDictionary).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(dictionaryService.update).toHaveBeenCalledWith(expect.objectContaining(dictionary));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDictionary>>();
      const dictionary = { id: 14247 };
      jest.spyOn(dictionaryFormService, 'getDictionary').mockReturnValue({ id: null });
      jest.spyOn(dictionaryService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ dictionary: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: dictionary }));
      saveSubject.complete();

      // THEN
      expect(dictionaryFormService.getDictionary).toHaveBeenCalled();
      expect(dictionaryService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDictionary>>();
      const dictionary = { id: 14247 };
      jest.spyOn(dictionaryService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ dictionary });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(dictionaryService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
