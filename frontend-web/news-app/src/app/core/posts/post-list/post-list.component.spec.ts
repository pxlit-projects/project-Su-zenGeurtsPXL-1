import {Post} from "../../../shared/models/posts/post.model";
import {PostService} from "../../../shared/services/post/post.service";
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
    { id: 1, title: 'Title', content: 'Content', userId: 1, category: 'ACADEMIC', createdAt: '2024-12-9 15:30:07', state: 'PUBLISHED'},
    { id: 2, title: 'Title2', content: 'Content2', userId: 4, category: 'SPORTS', createdAt: '2024-12-10 15:31:07', state: 'SUBMITTED'},
  ];

  const mockReversePosts: Post[] = [
    { id: 2, title: 'Title2', content: 'Content2', userId: 4, category: 'SPORTS', createdAt: '2024-12-10 15:31:07', state: 'SUBMITTED'},
    { id: 1, title: 'Title', content: 'Content', userId: 1, category: 'ACADEMIC', createdAt: '2024-12-9 15:30:07', state: 'PUBLISHED'},
  ];

  const filter: Filter = { content: 'con', author: 'Mi', date: '2024-12-10' };

  const filteredPosts: Post[] = [
    { id: 1, title: 'Title', content: 'Content', userId: 1, category: 'ACADEMIC', createdAt: '2024-12-9 15:30:07', state: 'PUBLISHED'},
    { id: 2, title: 'Title2', content: 'Content2', userId: 1, category: 'SPORTS', createdAt: '2024-12-10 15:31:07', state: 'SUBMITTED'},
  ];

  const mockReverseFilteredPosts: Post[] = [
    { id: 2, title: 'Title2', content: 'Content2', userId: 1, category: 'SPORTS', createdAt: '2024-12-10 15:31:07', state: 'SUBMITTED'},
    { id: 1, title: 'Title', content: 'Content', userId: 1, category: 'ACADEMIC', createdAt: '2024-12-9 15:30:07', state: 'PUBLISHED'},
  ];

  localStorage.setItem('userId', '1');

  beforeEach(() => {
    postServiceMock = jasmine.createSpyObj('PostService', ['getMyPosts', 'getPublishedPosts', 'getReviewablePosts', 'filterPublishedPosts', 'filterMyPosts', 'filterReviewablePosts']);

    routeMock = jasmine.createSpyObj('ActivatedRoute', [], {
      snapshot: { url: [new UrlSegment('post', {})] }
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

  it('should initialize "isMine" based on route URL', () => {
    // !MINE
    expect(component.isMine).toBe(false);

    // MINE
    routeMock.snapshot.url = [new UrlSegment('myPost', {})];
    fixture = TestBed.createComponent(PostListComponent);
    component = fixture.componentInstance;

    expect(component.isMine).toBe(true);
  });

  it('should initialize "review" based on route URL', () => {
    // !REVIEW
    expect(component.isToReview).toBe(false);

    // MINE
    routeMock.snapshot.url = [new UrlSegment('review', {})];
    fixture = TestBed.createComponent(PostListComponent);
    component = fixture.componentInstance;

    expect(component.isToReview).toBe(true);
  });

  it('should call fetchPosts on initialization', () => {
    spyOn(component, 'fetchPosts');
    fixture.detectChanges();
    expect(component.fetchPosts).toHaveBeenCalled();
  });

  it('should fetch posts correctly', () => {
    postServiceMock.getPublishedPosts.and.returnValue(of(mockPosts));
    postServiceMock.getMyPosts.and.returnValue(of(mockPosts));
    postServiceMock.getReviewablePosts.and.returnValue(of(mockPosts));

    // PUBLISHED
    component.fetchPosts();

    component.posts$.subscribe(posts => {
      expect(posts).toEqual(mockReversePosts);
    })

    expect(postServiceMock.getPublishedPosts).toHaveBeenCalled();

    // MINE
    routeMock.snapshot.url = [new UrlSegment('myPost', {})];
    fixture = TestBed.createComponent(PostListComponent);
    component = fixture.componentInstance;

    component.fetchPosts();

    expect(postServiceMock.getMyPosts).toHaveBeenCalled();

    // REVIEW
    routeMock.snapshot.url = [new UrlSegment('review', {})];
    fixture = TestBed.createComponent(PostListComponent);
    component = fixture.componentInstance;

    component.fetchPosts();

    expect(postServiceMock.getReviewablePosts).toHaveBeenCalled();
  });

  it('should filter posts based on the filter criteria correctly', () => {
    postServiceMock.filterPublishedPosts.and.returnValue(of(filteredPosts));
    postServiceMock.filterMyPosts.and.returnValue(of(filteredPosts));
    postServiceMock.filterReviewablePosts.and.returnValue(of(filteredPosts));

    // PUBLISHED
    component.handleFilter(filter);

    component.posts$.subscribe(posts => {
      expect(posts).toEqual(mockReverseFilteredPosts);
    })

    expect(postServiceMock.filterPublishedPosts).toHaveBeenCalledWith(filter);

    component.posts$.subscribe(data => {
      expect(data).toEqual(filteredPosts);
    });

    // MINE
    routeMock.snapshot.url = [new UrlSegment('myPost', {})];
    fixture = TestBed.createComponent(PostListComponent);
    component = fixture.componentInstance;

    component.handleFilter(filter);

    expect(postServiceMock.filterMyPosts).toHaveBeenCalledWith(filter);

    component.posts$.subscribe(data => {
      expect(data).toEqual(filteredPosts);
    });

    // REVIEW
    routeMock.snapshot.url = [new UrlSegment('review', {})];
    fixture = TestBed.createComponent(PostListComponent);
    component = fixture.componentInstance;

    component.handleFilter(filter);

    expect(postServiceMock.filterReviewablePosts).toHaveBeenCalledWith(filter);

    component.posts$.subscribe(data => {
      expect(data).toEqual(filteredPosts);
    });
  });
});
