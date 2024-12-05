import { InMemoryDbService } from 'angular-in-memory-web-api';

export class InmemoryDataService implements InMemoryDbService {
  createDb() {
    let users = [
      { id: 1, username: 'Dries Swinnen', email: 'dries.swinnen@lab.com', password: '123456'},
      { id: 2, username: 'John Doe', email: 'john.doe@example.com', password: '123456'},
      { id: 3, username: 'Alice Johnson', email: 'alice.johnson@example.com', password: '123456'},
      { id: 4, username: 'Michael Smith', email: 'michael.smith@example.com', password: '123456'},
      { id: 5, username: 'Laura Davis', email: 'laura.davis@example.com', password: '123456'},
      { id: 6, username: 'Alex Thompson', email: 'alex.thompson@example.com', password: '123456'},
      { id: 7, username: 'Sophie Clark', email: 'sophie.clark@example.com', password: '123456'},
      { id: 8, username: 'Anna Martinez', email: 'anna.martinez@example.com', password: '123456'},
      { id: 9, username: 'Pablo Hernandez', email: 'pablo.hernandez@example.com', password: '123456'}
    ];

    return {users};
  }
}
