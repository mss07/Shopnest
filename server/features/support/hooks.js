import { BeforeAll, AfterAll, Before } from '@cucumber/cucumber';
import express from 'express';
import Connection from '../../database/db.js';
import { configDotenv } from 'dotenv';
import defaultData from '../../default.js';
import { usersignup, userlogin } from '../../controller/usercontroller.js';
import { getproducts, getproductbyid } from '../../controller/productcontroller.js';
import cors from 'cors';
import bodyParser from 'body-parser';
import User from '../../model/userschema.js';

let server;

BeforeAll(async function() {
  // Initialize environment variables
  configDotenv();
  
  // Create Express app
  const app = express();
  
  app.use(cors());
  app.use(bodyParser.json({extended: true}));
  app.use(bodyParser.urlencoded({extended: true}));
  
  // Setup routes directly to avoid circular dependency with index.js
  app.post('/signup', usersignup);
  app.post('/login', userlogin);
  app.get('/products', getproducts);
  app.get('/product/:id', getproductbyid);
  
  const PORT = 8000;
  
  const USERNAME = process.env.DB_USERNAME;
  const PASSWORD = process.env.DB_PASSWORD;
  
  // Connect to database
  await Connection(USERNAME, PASSWORD);
  
  // Start server
  server = app.listen(PORT, () => {
    console.log(`[Cucumber] Test server running on port ${PORT}`);
  });
  
  // Load default data
  defaultData();
});

Before(async function() {
  // Clean up test users before each scenario
  const testUsernames = ['johndoe', 'existinguser', 'testuser', 'bobwilson', 'validuser', 'nonexistentuser', 'tomhardy'];
  try {
    await User.deleteMany({ username: { $in: testUsernames } });
    console.log('[Cucumber] Test users cleaned up');
  } catch (error) {
    console.log('[Cucumber] Error cleaning up test users:', error.message);
  }
});

AfterAll(async function() {
  if (server) {
    await new Promise(resolve => server.close(resolve));
    console.log('[Cucumber] Test server stopped');
  }
});
