# schnauf-r frontend
The ultimate Schnaufer-Frontend, by SchnaufRs for SchnaufRs, powered by RSocket

## Local execution
### Install yarn and dependencies: 
You’ll need to have Node 8.12.0 or later and yarn 1.10.1 or later on your local development machine 

Node:  [https://nodejs.org/en/download/](Download node)<br>
Yarn: [https://yarnpkg.com/en/docs/install](Download yarn)<br>
 
`yarn install` will download all required dependencies on your local machine.

### Available Scripts

### `yarn start`
Runs the app in development mode.<br>
Open [http://localhost:1234](http://localhost:1234) to view it in the browser.

### `yarn test`
Runs the tests

### `yarn test.watch`
Runs the test watcher in an interactive mode.<br>

### `yarn build`
Builds the app for production to the `build/output` folder.<br>
It correctly bundles React in production mode and optimizes the build.


## Deploy
To deploy you have to run `yarn` and `yarn build`. Afterwards you can run
`docker build -t "gcr.io/main-stack-241307/schnaufr-frontend:latest" .` to build the image.
Push it afterwards with `docker push gcr.io/main-stack-241307/schnaufr-frontend:latest`.
To get the latest version simply delete the pod `schnaufr-gateway`.
