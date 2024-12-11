import {Post} from "../../../shared/models/post.model";
import {PostService} from "../../../shared/services/post.service";
import {PostListComponent} from "./post-list.component";
import {Filter} from "../../../shared/models/filter.model";

import {ComponentFixture, TestBed } from "@angular/core/testing";
import {of} from "rxjs";
import {ActivatedRoute, UrlSegment} from "@angular/router";

describe('PostListComponent', () => {
  let component: PostListComponent;
  let fixture: ComponentFixture<PostListComponent>;
  let postServiceMock: jasmine.SpyObj<PostService>;
  let routeMock: jasmine.SpyObj<ActivatedRoute>;
  const mockPosts: Post[] = [
    new Post(1, 'Title1', 'Content...', 1, 'ACADEMIC', '2024-12-10 15:30:07', 'PUBLISHED'),
    new Post(2, 'Title1', 'Content...', 1, 'ACADEMIC', '2024-12-10 15:30:07', 'PUBLISHED')
  ];

  beforeEach(() => {
    postServiceMock = jasmine.createSpyObj('PostService', ['getPublishedPosts', 'getPostsByUserId', 'filterPosts']);

    routeMock = jasmine.createSpyObj('ActivatedRoute', [], {
      snapshot: { url: [new UrlSegment('posts', {})] }
    });

    TestBed.configureTestingModule({
      imports: [PostListComponent],
      providers: [
        { provide: PostService, useValue: postServiceMock },
        { provide: ActivatedRoute, useValue: routeMock }
      ]
    });

    fixture = TestBed.createComponent(PostListComponent);
    component = fixture.componentInstance;
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize "mine" based on route URL', () => {
    expect(component.mine).toBe(false);
    routeMock.snapshot.url = [new UrlSegment('post', {}), new UrlSegment('mine', {})];
    fixture = TestBed.createComponent(PostListComponent);
    component = fixture.componentInstance;
    expect(component.mine).toBe(true);
  });

  it('should call fetchPosts on initialization', () => {
    spyOn(component, 'fetchPosts');
    fixture.detectChanges();
    expect(component.fetchPosts).toHaveBeenCalled();
  });

  it('should fetch published posts when "mine" is false', () => {
    postServiceMock.getPublishedPosts.and.returnValue(of(mockPosts));

    component.fetchPosts();

    expect(postServiceMock.getPublishedPosts).toHaveBeenCalled();
    component.posts$.subscribe(data => {
      expect(data).toEqual(mockPosts);
    });
  });

  it('should fetch posts by user id when "mine" is true', () => {
    routeMock.snapshot.url = [new UrlSegment('post', {}), new UrlSegment('mine', {})];
    localStorage.setItem('userId', '1');
    postServiceMock.getPostsByUserId.and.returnValue(of(mockPosts));
    fixture = TestBed.createComponent(PostListComponent);
    component = fixture.componentInstance;

    component.fetchPosts();

    expect(postServiceMock.getPostsByUserId).toHaveBeenCalledWith('1');
    component.posts$.subscribe(data => {
      expect(data).toEqual(mockPosts);
    });
  });

  it('should filter posts based on the filter criteria', () => {
    const filter: Filter = { content: 'con', author: 'Mi', category: 'ACA' };
    const filteredPosts: Post[] = [new Post(1, 'Title1', 'Content...', 1, 'ACADEMIC', '2024-12-10 15:30:07', 'PUBLISHED')];
    postServiceMock.filterPosts.and.returnValue(of(filteredPosts));

    component.handleFilter(filter);

    expect(postServiceMock.filterPosts).toHaveBeenCalledWith(filter, false);
    component.posts$.subscribe(data => {
      expect(data).toEqual(filteredPosts);
    });
  });
});

