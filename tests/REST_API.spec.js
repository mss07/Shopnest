import {test,expect} from "@playwright/test";

let url='https://shopnest-2gb2.onrender.com/products/';
test('Rest API with GET method', async ({ request }) => {
  const response=await request.get(url);
  expect(response.status()).toBe(200);
  const responseBody = await response.json(); 
//   console.log(responseBody);
  expect (responseBody[0].id).toBe('product3')
});
test('Rest API with POST method', async ({ request }) => {
  const response=await request.get(url);
  expect(response.status()).toBe(200);
  const responseBody = await response.json(); 
//   console.log(responseBody);
  expect (responseBody[0].id).toBe('product3')
});

test('Rest API with POST method (Login)', async ({ request }) => {
  const loginUrl = 'https://shopnest-2gb2.onrender.com/login';

  const response = await request.post(loginUrl, {
    data: {
      username: 'rahul22',
      password: '12345678'  
    }
  });

  expect(response.status()).toBe(200);

  const responseBody = await response.json();
  console.log(responseBody);
  
  expect(responseBody.data.username).toBe('rahul22');
});
