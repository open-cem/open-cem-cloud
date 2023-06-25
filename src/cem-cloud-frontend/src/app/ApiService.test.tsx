import { ApiBuilder } from "./ApiService";

describe('Api builder', () => {
  const baseUrl = "";
  let fetchMock: any = undefined;

  let mockApiUri = jest.fn(function () {
    return baseUrl;
  });
  jest.mock('./AppSettings', () => {
    return jest.fn().mockImplementation(() => {
      return { getApiUri: mockApiUri };
    });
  });

  beforeEach(() => {
    const assetsFetchMock = () => Promise.resolve({
      ok: true,
      status: 200,
      headers: new Headers([["Location", "testHeader123"]]),
      json: async () => ({ id: 9, name: "TestObject" })
    } as Response);

    fetchMock = jest.spyOn(global, "fetch")
      .mockImplementation(assetsFetchMock);
  });

  afterEach(() => {
    jest.restoreAllMocks();
  });

  it('empty builder does call fetch', () => {
    const emptyRequestInit = {
      body: undefined,
      headers: [],
      method: "GET"
    };
    const builder = new ApiBuilder<any>();

    builder.fetchBody();

    expect(fetchMock).toHaveBeenCalled();
    expect(fetchMock).toHaveBeenCalledWith(baseUrl, emptyRequestInit);
  });

  it('add authorization headers', () => {
    const requestWithAuthHeader = {
      body: undefined,
      headers: [["authorization", "Bearer abc123"]],
      method: "GET"
    };
    const builder = new ApiBuilder<any>()
      .withAuthorization("abc123");

    builder.fetchBody();

    expect(fetchMock).toHaveBeenCalled();
    expect(fetchMock).toHaveBeenCalledWith(baseUrl, requestWithAuthHeader);
  });

  it('add uri', () => {
    const emptyRequestInit = {
      body: undefined,
      headers: [],
      method: "GET"
    };
    const builder = new ApiBuilder<any>()
      .withUri("test");

    builder.fetchBody();

    expect(fetchMock).toHaveBeenCalled();
    expect(fetchMock).toHaveBeenCalledWith(`${baseUrl}test`, emptyRequestInit);
  });

  it('add body', () => {
    const request = {
      body: "{\"name\":\"test\"}",
      headers: [["Content-Type", "application/json"]],
      method: "GET"
    };
    const builder = new ApiBuilder<any>()
      .withBody({ name: "test" });

    builder.fetchBody();

    expect(fetchMock).toHaveBeenCalled();
    expect(fetchMock).toHaveBeenCalledWith(baseUrl, request);
  });

  it('add set method', () => {
    const request = {
      body: undefined,
      headers: [],
      method: "OPTIONS"
    };
    const builder = new ApiBuilder<any>()
      .withMethod("OPTIONS");

    builder.fetchBody();

    expect(fetchMock).toHaveBeenCalled();
    expect(fetchMock).toHaveBeenCalledWith(baseUrl, request);
  });

  it('as POST request', () => {
    const request = {
      body: undefined,
      headers: [],
      method: "POST"
    };
    const builder = new ApiBuilder<any>()
      .post();

    builder.fetchBody();

    expect(fetchMock).toHaveBeenCalled();
    expect(fetchMock).toHaveBeenCalledWith(baseUrl, request);
  });

  it('as PUT request', () => {
    const request = {
      body: undefined,
      headers: [],
      method: "PUT"
    };
    const builder = new ApiBuilder<any>()
      .put();

    builder.fetchBody();

    expect(fetchMock).toHaveBeenCalled();
    expect(fetchMock).toHaveBeenCalledWith(baseUrl, request);
  });

  it('fetch Location header', () => {
    const location = new ApiBuilder<any>()
      .fetchLocationHeader();

    expect(location).toStrictEqual(Promise.resolve("testHeader123"));
  });

  it('fetch body', () => {
    const obj = new ApiBuilder<{ id: number, name: string }>()
      .fetchBody();

    expect(obj).toStrictEqual(Promise.resolve({ id: 9, name: "TestObject" }));
  });
});