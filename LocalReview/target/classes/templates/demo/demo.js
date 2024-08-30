const getQueryValue = (key, defaultValue) => {
  let value = defaultValue;
  if (window.location.search) {
      const params = new URLSearchParams(window.location.search);
      value = params.get(key) || defaultValue;
  }
  return value;
};
