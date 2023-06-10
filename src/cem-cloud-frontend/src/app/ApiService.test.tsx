import { ApiBuilder } from "./ApiService";

describe('Api builder', () => {
  const baseUrl = "";
  let fetchMock: any = undefined;

  let mockApiUri = jest.fn(function() {
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
      json: async () => new Object()
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

  it('builder add authorization headers', () => {
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

  it('builder add uri', () => {
    const emptyRequestHeaders = { 
      body: undefined,
      headers: [],
      method: "GET"
    };
    const builder = new ApiBuilder<any>()
      .withUri("test");

    builder.fetchBody();

    expect(fetchMock).toHaveBeenCalled();
    expect(fetchMock).toHaveBeenCalledWith(`${baseUrl}test`, emptyRequestHeaders);
  });
});