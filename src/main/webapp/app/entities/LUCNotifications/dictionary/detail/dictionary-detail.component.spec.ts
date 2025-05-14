import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { DictionaryDetailComponent } from './dictionary-detail.component';

describe('Dictionary Management Detail Component', () => {
  let comp: DictionaryDetailComponent;
  let fixture: ComponentFixture<DictionaryDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DictionaryDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./dictionary-detail.component').then(m => m.DictionaryDetailComponent),
              resolve: { dictionary: () => of({ id: 14247 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(DictionaryDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DictionaryDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load dictionary on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', DictionaryDetailComponent);

      // THEN
      expect(instance.dictionary()).toEqual(expect.objectContaining({ id: 14247 }));
    });
  });

  describe('PreviousState', () => {
    it('should navigate to previous state', () => {
      jest.spyOn(window.history, 'back');
      comp.previousState();
      expect(window.history.back).toHaveBeenCalled();
    });
  });
});
