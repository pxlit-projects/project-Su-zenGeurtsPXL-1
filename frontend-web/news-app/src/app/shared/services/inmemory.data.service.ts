import { InMemoryDbService } from 'angular-in-memory-web-api';

export class InmemoryDataService implements InMemoryDbService {
  createDb() {
    let users = [
      { id: 1, name: 'Dries Swinnen', email: 'dries.swinnen@lab.com', city: 'Pelt', address: 'Stationstraat 36', country: 'Belgium', vat: 21, isLoyal: true, avatar: 'default.png' },
      { id: 2, name: 'John Doe', email: 'john.doe@example.com', city: 'New York', address: '123 Main St', country: 'USA', vat: 21, isLoyal: false, avatar: 'default.png' },
      { id: 3, name: 'Alice Johnson', email: 'alice.johnson@example.com', city: 'Los Angeles', address: '456 Elm St', country: 'USA', vat: 6, isLoyal: false, avatar: 'default.png' },
      { id: 4, name: 'Michael Smith', email: 'michael.smith@example.com', city: 'London', address: '789 Oak St', country: 'United Kingdom', vat: 20, isLoyal: false, avatar: 'default.png' },
      { id: 5, name: 'Laura Davis', email: 'laura.davis@example.com', city: 'Liverpool', address: '101 Maple St', country: 'United Kingdom', vat: 6, isLoyal: false, avatar: 'default.png' },
      { id: 6, name: 'Alex Thompson', email: 'alex.thompson@example.com', city: 'Los Angeles', address: '222 Cedar St', country: 'USA', vat: 19, isLoyal: false, avatar: 'default.png' },
      { id: 7, name: 'Sophie Clark', email: 'sophie.clark@example.com', city: 'Pelt', address: '333 Birch St', country: 'Belgium', vat: 19, isLoyal: false, avatar: 'default.png' },
      { id: 8, name: 'Anna Martinez', email: 'anna.martinez@example.com', city: 'Madrid', address: '555 Cedar St', country: 'Spain', vat: 23, isLoyal: false, avatar: 'default.png' },
      { id: 9, name: 'Pablo Hernandez', email: 'pablo.hernandez@example.com', city: 'Barcelona', address: '666 Elm St', country: 'Spain', vat: 18, isLoyal: false, avatar: 'default.png' }
    ];

    return {users};
  }
}
