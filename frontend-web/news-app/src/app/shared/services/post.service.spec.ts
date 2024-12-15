import {Post} from "../models/post.model";
import {PostService} from "./post.service";
import {Filter} from "../models/filter.model";

import {HttpTestingController, provideHttpClientTesting} from "@angular/common/http/testing";
import {TestBed} from "@angular/core/testing";
import {provideHttpClient} from "@angular/common/http";
import {of} from "rxjs";


describe('PostService', () => {
  let service: PostService;
  let httpTestingController: HttpTestingController;

  let mockPosts: Post[] = [];

  const mockCategories: string[] = [
    'STUDENT',
    'SPORTS',
    'ALUMNI'
  ]

  beforeEach(() => {
    mockPosts = [
      { id: 1, title: 'Title', content: 'About a student', userId: 1, category: 'STUDENT', createdAt: '2024-12-10 15:30:07', state: 'DRAFTED'},
      { id: 2, title: 'Title', content: 'About sport', userId: 1, category: 'SPORTS', createdAt: '2024-12-5 15:30:07', state: 'SUBMITTED'},
      { id: 3, title: 'Title', content: 'About alumni', userId: 1, category: 'ALUMNI', createdAt: '2024-12-7 15:30:07', state: 'PUBLISHED'}
    ];

    TestBed.configureTestingModule({
      providers: [
        PostService,
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    });
    service = TestBed.inject(PostService);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  // getPost()
  it('should retrieve a single post by ID via GET', () => {
    const postId = 1;

    service.getPost(postId).subscribe(post => {
      expect(post).toEqual(mockPosts[0]);
    });

    const req = httpTestingController.expectOne(service.api + '/' + postId);
    expect(req.request.method).toBe('GET');
    req.flush(mockPosts[0]);
  });


  // getCategories()
  it('should retrieve categories via GET', () => {
    service.getCategories().subscribe(categories => {
      expect(categories).toEqual(mockCategories);
    });

    const req = httpTestingController.expectOne(service.api + '/category');
    expect(req.request.method).toBe('GET');
    req.flush(mockCategories);
  });

  // getPublishedPosts()
  it('should retrieve published posts via GET', () => {
    service.getPublishedPosts().subscribe(posts => {
      expect(posts).toEqual(mockPosts);
    });

    const req = httpTestingController.expectOne(service.api + '/published');
    expect(req.request.method).toBe('GET');
    req.flush(mockPosts);
  });

  // getMyPosts()
  it('should retrieve posts by USERID via GET', () => {
    const userId = '1';

    service.getMyPosts(userId).subscribe(post => {
      expect(post).toEqual(mockPosts);
    });

    const req = httpTestingController.expectOne(service.api + '/user/' + userId);
    expect(req.request.method).toBe('GET');
    req.flush(mockPosts);
  });

  // addPost()
  it('should add a post via POST', () => {
    const newPost = { id: 4, title: 'Title', content: 'Content...', userId: 1, category: 'ACADEMIC', createdAt: '2024-12-18 15:30:07', state: 'DRAFTED'};

    service.addPost(newPost).subscribe(post => {
      expect(post).toEqual(newPost);
    });

    const req = httpTestingController.expectOne(service.api);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(newPost);
    req.flush(newPost);
  });

  // submitPost()
  it('should submit a post via POST', () => {
    const postId = 1;
    const userId = 1;

    service.submitPost(postId, userId).subscribe();

    const req = httpTestingController.expectOne(service.api + '/submit/' + postId);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(userId);
    req.flush(null);
  });

  it('should throw error when submitting a post without a USERID', () => {
    const postId = 1;
    const userId = null;

    expect(() => service.submitPost(postId, userId).subscribe()).toThrowError('Nobody is signed in.');
  });

  // editPost()
  it('should update a post via PUT', () => {
    const postId = 1;
    const updatedPost = { ...mockPosts[0], content: 'Updated content' };

    service.editPost(postId, updatedPost).subscribe(post => {
      expect(post).toEqual(updatedPost);
    });

    const req = httpTestingController.expectOne(service.api + '/' + postId);
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual(updatedPost);
    req.flush(updatedPost);
  });

  // filterPosts()
  it('should filter posts based on the filter criteria', () => {
    const filter: Filter = { content: 'About', author: 'Milan', category: 'spo' };

    service.filterPublishedPosts(filter).subscribe(posts => {
      expect(posts).toEqual([mockPosts[1]]);
    });

    const req = httpTestingController.expectOne(service.api + '/published');
    req.flush(mockPosts);
  });

  it('should filter posts by USERID based on the filter criteria', () => {
    const userId = '1';
    const filter: Filter = { content: 'About', author: 'Milan', category: 'spo' };

    service.filterMyPosts(filter, userId).subscribe(posts => {
      expect(posts).toEqual([mockPosts[1]]);
    });

    const req = httpTestingController.expectOne(service.api + '/user/' + userId);
    req.flush(mockPosts);
  });

  it('should match post correctly with the filter criteria', () => {
    const filter: Filter = { content: 'About', author: 'Milan', category: 'spo' };
    expect(service.isPostMatchingFilter(mockPosts[0], filter)).toBeFalse();
    expect(service.isPostMatchingFilter(mockPosts[1], filter)).toBeTrue();
    expect(service.isPostMatchingFilter(mockPosts[2], filter)).toBeFalse();
  });

  it('should not filter when filter criteria of user is broken', () => {
    mockPosts.push({ id: 4, title: 'Title', content: 'Content...', userId: 9999, category: 'ACADEMIC', createdAt: '2024-12-10 15:30:07', state: 'PUBLISHED'});
    const filter: Filter = { content: 'About', author: 'Milan', category: 'spo' };

    expect(service.isPostMatchingFilter(mockPosts[mockPosts.length - 1], filter)).toBeFalse();
  })

  // transformDate()
  it('should format strings to PascalCasing correctly', () => {
    const word = '2024-12-10 15:30:07'
    const expectedWord = 'Tuesday, December 10, 2024 at 3:30 PM'
    expect(service.transformDate(word)).toBe(expectedWord);
  })

  // toPascalCasing()
  it('should format strings to PascalCasing correctly', () => {
    const word = 'ACADEMIC'
    const expectedWord = 'Academic'
    expect(service.toPascalCasing(word)).toBe(expectedWord);
  })

  // orderToMostRecent()
  it('should order the array correctly', () => {
    const expectedPosts: Post[] = [
      { id: 1, title: 'Title', content: 'About a student', userId: 1, category: 'STUDENT', createdAt: '2024-12-10 15:30:07', state: 'DRAFTED'},
      { id: 3, title: 'Title', content: 'About alumni', userId: 1, category: 'ALUMNI', createdAt: '2024-12-7 15:30:07', state: 'PUBLISHED'},
      { id: 2, title: 'Title', content: 'About sport', userId: 1, category: 'SPORTS', createdAt: '2024-12-5 15:30:07', state: 'SUBMITTED'}
    ];

    service.orderToMostRecent(of(mockPosts)).subscribe(posts => {
      expect(posts).toEqual(expectedPosts);
    });
  });
});
