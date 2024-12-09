import {Post} from "../models/post.model";
import {PostService} from "./post.service";
import {Filter} from "../models/filter.model";

import {HttpTestingController, provideHttpClientTesting} from "@angular/common/http/testing";
import {TestBed} from "@angular/core/testing";
import {provideHttpClient} from "@angular/common/http";


describe('PostService', () => {
  let service: PostService;
  let httpTestingController: HttpTestingController;

  const mockPosts: Post[] = [
    new Post('Title 01', 'About a student', 1, 'STUDENT', '2024-12-10 15:30:07', 'DRAFTED'),
    new Post('Title 02', 'About sport', 1, 'SPORTS', '2024-12-10 15:30:07', 'SUBMITTED'),
    new Post('Title 03', 'About alumni', 1, 'ALUMNI', '2024-12-10 15:30:07', 'PUBLISHED')
  ];

  const mockCategories: string[] = [
    'STUDENT',
    'SPORTS',
    'ALUMNI'
  ]

  beforeEach(() => {
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
    const newPost = new Post('Title', 'Content...', 1, 'ACADEMIC', '2024-12-10 15:30:07', 'DRAFTED');

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
    mockPosts.push(new Post('Title 03', 'About sport', 9999, 'SPORTS', '2024-12-10 15:30:07', 'DRAFTED'))
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

});
