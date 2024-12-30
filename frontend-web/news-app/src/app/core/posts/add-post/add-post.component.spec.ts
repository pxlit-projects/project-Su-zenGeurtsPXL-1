import {Post} from "../../../shared/models/post.model";
import {PostRequest} from "../../../shared/models/post-request.model";
import {PostService} from "../../../shared/services/post.service";
import {AddPostComponent} from "./add-post.component";

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { ReactiveFormsModule } from '@angular/forms';
import { of } from 'rxjs';

describe('AddPostComponent', () => {
  let component: AddPostComponent;
  let fixture: ComponentFixture<AddPostComponent>;
  let postServiceMock: jasmine.SpyObj<PostService>;
  let routerMock: jasmine.SpyObj<Router>;

  beforeEach(() => {
    localStorage.setItem('userId', '1');
    postServiceMock = jasmine.createSpyObj('PostService', ['addPost', 'getCategories']);
    routerMock = jasmine.createSpyObj('Router', ['navigate']);

    TestBed.configureTestingModule({
      imports: [AddPostComponent, ReactiveFormsModule],
      providers: [
        { provide: PostService, useValue: postServiceMock },
        { provide: Router, useValue: routerMock }
      ]
    });

    fixture = TestBed.createComponent(AddPostComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create the form with required controls and validators', () => {
    expect(component.postForm).toBeTruthy();
    expect(component.postForm.controls['title'].valid).toBeFalse();
    expect(component.postForm.controls['content'].valid).toBeFalse();
    expect(component.postForm.controls['category'].valid).toBeFalse();
  });

  it('should call addPost on form submit and navigate on success', () => {
    const postRequest = {
      title: 'Title',
      content: 'Content...',
      category: 'ACADEMIC',
    };

    const mockPost = {
      title: 'Title',
      content: 'Content...',
      userId: 1,
      category: 'ACADEMIC',
      createdAt: '2024-12-10 15:30:07',
      state: 'DRAFTED'
    };

    component.postForm.setValue(postRequest);
    postServiceMock.addPost.and.returnValue(of(mockPost as Post));

    component.onSubmit();

    expect(postServiceMock.addPost).toHaveBeenCalledWith(postRequest as PostRequest);

    expect(component.postForm.pristine).toBeTrue();
    expect(routerMock.navigate).toHaveBeenCalledWith(['/myPost']);
  });

  it('should navigate back on cancel', () => {
    component.cancel();
    expect(routerMock.navigate).toHaveBeenCalledWith(['/myPost']);
  });
});
