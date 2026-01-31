export const APP_DOMAIN = "http://localhost:4106";
export const GATE_WAY_ENDPOINT =  "http://localhost:8106";
export const environment = {
  production: false,
  payrollEndPoint: GATE_WAY_ENDPOINT + '/payroll/v1/',
  employeeEndPoint :  "http://192.168.10.56:8090" + "/employee/v1",
  portalEndPoint : GATE_WAY_ENDPOINT + "/portal/v1",
  centrinoUrl: 'http://192.168.10.56:8090' +'/centrino/api/',
  apiBaseUrl:'http://192.168.10.56:8090',
  keycloakApi:'http://192.168.10.56:8090/resource-manager/api/v1/resource',
  returnUri: APP_DOMAIN + '/landing/home',
  logoutUri: APP_DOMAIN + '/login',
  appId: '106',
  keycloak: {
    url: 'http://192.168.10.56:9080',
    realm: 'MicroCube_dev',
    clientId: 'Payroll-UI'
    //clientId: 'FinBookUI'
  },
  reportManagementApiUrl: 'http://192.168.10.56:8090/centrino/api', // Report Management Backend API
  reportGenerationApiUrl: 'http://192.168.10.56:8090',
  sessionUrl:'http://192.168.10.56:8090/centrino/api/office-session/getOfficeSession',
  novu_identifier: '1uXpKIJUa3Rg',
  novu_socket: 'http://192.168.10.56:3002',
  novu_api: 'http://192.168.10.56:3000/novu/api',
  novu_service: 'http://192.168.10.56:8088/notify'
};

