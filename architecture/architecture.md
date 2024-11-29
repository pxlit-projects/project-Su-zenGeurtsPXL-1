# Architecture
![Architecture](https://github.com/pxlit-projects/project-Su-zenGeurtsPXL-1/blob/main/architecture/architecture.drawio.svg)

### Synchronous communications
- When fetching a post by id with its reviews from the `Post Service`, the reviews are fetched by id from the `Review Service`
- When fetching a post by id with its comments from the `Post Service`, the comments are fetched by id from the `Comment Service`
- When a post is approved/rejected via the `Review Service`, the status of the post is updated via the `Post Service`
- When reviewing a post with the `Review Service`, the status of the post is checked via the `Post Service` &rarr; Meaning only submitted posts are reviewable (relevant for User Story 2)

### Asynchronous communications
- When approving/rejecting a post with the `Review Service`, this review of the post is send to the queue for the `Post Service` to read.
