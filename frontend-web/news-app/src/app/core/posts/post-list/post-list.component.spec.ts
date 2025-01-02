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
    { id: 1, title: 'Title', content: 'Content...', userId: 1, category: 'ACADEMIC', createdAt: '2024-12-10 15:30:07', state: 'PUBLISHED'},
    { id: 2, title: 'Title2', content: 'Content2...', userId: 4, category: 'SPORTS', createdAt: '2024-12-10 15:31:07', state: 'SUBMITTED'},
  ];

  const mockReversePosts: Post[] = [
    { id: 2, title: 'Title2', content: 'Content2...', userId: 4, category: 'SPORTS', createdAt: '2024-12-10 15:31:07', state: 'SUBMITTED'},
    { id: 1, title: 'Title', content: 'Content...', userId: 1, category: 'ACADEMIC', createdAt: '2024-12-10 15:30:07', state: 'PUBLISHED'},
  ];

  beforeEach(() => {
    localStorage.setItem('userId', '1');
    postServiceMock = jasmine.createSpyObj('PostService', ['getPublishedPosts', 'getMyPosts', 'getSubmittedPosts', 'filterPublishedPosts', 'filterMyPosts', 'filterSubmittedPosts', 'orderToMostRecent']);
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
    // !mine
    expect(component.mine).toBe(false);

    // mine
    routeMock.snapshot.url = [new UrlSegment('myPost', {})];
    fixture = TestBed.createComponent(PostListComponent);
    component = fixture.componentInstance;

    expect(component.mine).toBe(true);
  });

  it('should initialize "review" based on route URL', () => {
    // !review
    expect(component.review).toBe(false);

    // mine
    routeMock.snapshot.url = [new UrlSegment('review', {})];
    fixture = TestBed.createComponent(PostListComponent);
    component = fixture.componentInstance;

    expect(component.review).toBe(true);
  });

  it('should call fetchPosts on initialization', () => {
    spyOn(component, 'fetchPosts');
    fixture.detectChanges();
    expect(component.fetchPosts).toHaveBeenCalled();
  });

  it('should fetch posts correctly', () => {
    postServiceMock.getPublishedPosts.and.returnValue(of(mockPosts));
    postServiceMock.getMyPosts.and.returnValue(of(mockPosts));
    postServiceMock.orderToMostRecent.and.returnValue(of(mockReversePosts));

    // if (!mine) getPublishedPosts
    component.fetchPosts();

    expect(postServiceMock.getPublishedPosts).toHaveBeenCalled();
    expect(postServiceMock.orderToMostRecent).toHaveBeenCalled();
    component.posts$.subscribe(data => {
      expect(data).toEqual(mockReversePosts);
    });

    // if (mine) getMyPosts
    routeMock.snapshot.url = [new UrlSegment('myPost', {})];
    fixture = TestBed.createComponent(PostListComponent);
    component = fixture.componentInstance;

    component.fetchPosts();

    expect(postServiceMock.getMyPosts).toHaveBeenCalled();
    expect(postServiceMock.orderToMostRecent).toHaveBeenCalled();
    component.posts$.subscribe(data => {
      expect(data).toEqual(mockReversePosts);
    });

    // if (review) getSubmittedPosts
    routeMock.snapshot.url = [new UrlSegment('review', {})];
    fixture = TestBed.createComponent(PostListComponent);
    component = fixture.componentInstance;

    component.fetchPosts();

    expect(postServiceMock.getSubmittedPosts).toHaveBeenCalled();
    expect(postServiceMock.orderToMostRecent).toHaveBeenCalled();
    component.posts$.subscribe(data => {
      expect(data).toEqual(mockReversePosts);
    });
  });

  it('should filter posts based on the filter criteria correctly', () => {
    const filter: Filter = { content: 'con', author: 'Mi', date: '2024-12-10' };
    const filteredPosts: Post[] = [
      { id: 1, title: 'Title', content: 'Content...', userId: 1, category: 'ACADEMIC', createdAt: '2024-12-10 15:30:07', state: 'PUBLISHED'},
    ];
    postServiceMock.filterPublishedPosts.and.returnValue(of(filteredPosts));
    postServiceMock.filterMyPosts.and.returnValue(of(filteredPosts));
    postServiceMock.orderToMostRecent.and.returnValue(of(filteredPosts));

    // if (!mine) filterPublishedPosts
    component.handleFilter(filter);

    expect(postServiceMock.filterPublishedPosts).toHaveBeenCalledWith(filter);
    expect(postServiceMock.orderToMostRecent).toHaveBeenCalled();

    component.posts$.subscribe(data => {
      expect(data).toEqual(filteredPosts);
    });

    // if (mine) filterMyPosts
    routeMock.snapshot.url = [new UrlSegment('myPost', {})];
    fixture = TestBed.createComponent(PostListComponent);
    component = fixture.componentInstance;

    component.handleFilter(filter);

    expect(postServiceMock.filterMyPosts).toHaveBeenCalledWith(filter);

    component.posts$.subscribe(data => {
      expect(data).toEqual(filteredPosts);
    });

    // if (review) getSubmittedPosts
    routeMock.snapshot.url = [new UrlSegment('review', {})];
    fixture = TestBed.createComponent(PostListComponent);
    component = fixture.componentInstance;

    component.handleFilter(filter);

    expect(postServiceMock.filterSubmittedPosts).toHaveBeenCalledWith(filter);

    component.posts$.subscribe(data => {
      expect(data).toEqual(filteredPosts);
    });
  });
});
