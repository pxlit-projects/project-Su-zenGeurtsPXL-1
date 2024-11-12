# Architecture
![Architecture](https://github.com/pxlit-projects/project-Su-zenGeurtsPXL-1/blob/main/architecture/architecture.drawio.svg)

### Synchronous communications
- When fetching a post by id with its reviews from the `Post Service`, the reviews are fetched by id from the `Review Service`
- When fetching a post by id with its comments from the `Post Service`, the comments are fetched by id from the `Comment Service`
- When a post is approved/rejected via the `Review Service`, the status of the post is updated via the `Post Service`

### Asynchronous communications
- When approving a post with the `Review Service`, the editor of the post is notified
- When rejecting a post with the `Review Service`, the editor of the post is notified
- When adding a review with the `Review Service`, the editor of the post is notified

# Entities
![UML Diagram of enitities](https://github.com/pxlit-projects/project-Su-zenGeurtsPXL-1/blob/main/architecture/entities.drawio.svg)

### User roles
- Editor
- Editor-in-chief
- User

# End points
### Post Service
- @PostMapping("/") add [[US1]](https://github.com/pxlit-projects/project-Su-zenGeurtsPXL-1/issues/2)[[US2]](https://github.com/pxlit-projects/project-Su-zenGeurtsPXL-1/issues/4)
- @PutMapping("/{id}") edit [[US3]](https://github.com/pxlit-projects/project-Su-zenGeurtsPXL-1/issues/5)
- @GetMapping("/") findAll [[US1]](https://github.com/pxlit-projects/project-Su-zenGeurtsPXL-1/issues/2)
- @GetMapping("/{id}") findById [[US3]](https://github.com/pxlit-projects/project-Su-zenGeurtsPXL-1/issues/4)
- @GetMapping("/published") findAllPublished [[US4]](https://github.com/pxlit-projects/project-Su-zenGeurtsPXL-1/issues/6)
- @GetMapping("/submitted") findAllSubmitted [[US7]](https://github.com/pxlit-projects/project-Su-zenGeurtsPXL-1/issues/8)
- @GetMapping("/enums/categories") findAllCategories [[US1]](https://github.com/pxlit-projects/project-Su-zenGeurtsPXL-1/issues/2)
- @GetMapping("/{id}/with-reviews") findByIdWithReviews &rarr; find Review by Id [[US8]](https://github.com/pxlit-projects/project-Su-zenGeurtsPXL-1/issues/9)
- @GetMapping("/{id}/with-comments") findByIdWithComments &rarr; find Comment by Id [[US11]](https://github.com/pxlit-projects/project-Su-zenGeurtsPXL-1/issues/12)

### Review Service
- @PostMapping("/") review [[US7]](https://github.com/pxlit-projects/project-Su-zenGeurtsPXL-1/issues/8) &rarr; edit Post [[US7]](https://github.com/pxlit-projects/project-Su-zenGeurtsPXL-1/issues/8) + notify editor [[US8]](https://github.com/pxlit-projects/project-Su-zenGeurtsPXL-1/issues/9)
- @PostMapping("/") add [[US9]](https://github.com/pxlit-projects/project-Su-zenGeurtsPXL-1/issues/10) &rarr; notify editor [[US8]](https://github.com/pxlit-projects/project-Su-zenGeurtsPXL-1/issues/9)
- @GetMapping("/{id}") findById [[US8]](https://github.com/pxlit-projects/project-Su-zenGeurtsPXL-1/issues/9)

### Comment Service
- @PostMapping("/") add [[US10]](https://github.com/pxlit-projects/project-Su-zenGeurtsPXL-1/issues/11)
- @GetMapping("/{id}") findById [[US11]](https://github.com/pxlit-projects/project-Su-zenGeurtsPXL-1/issues/12)
- @PutMapping("/{id}") edit [[US12]](https://github.com/pxlit-projects/project-Su-zenGeurtsPXL-1/issues/13)
- @DeleteMapping("/{id}") delete [[US12]](https://github.com/pxlit-projects/project-Su-zenGeurtsPXL-1/issues/13)
