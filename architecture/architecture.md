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