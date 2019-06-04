module.exports = {
  env: {
    es6: true,
    node: true,
    'jest/globals': true
  },
  parserOptions: {
    ecmaVersion: 9
  },
  plugins: ['jest'],
  extends: ['eslint:recommended', 'plugin:jest/recommended'],
  rules: {
    eqeqeq: ['error', 'always'],
    indent: [
      'error',
      2,
      {
        SwitchCase: 1
      }
    ],
    'linebreak-style': ['error', 'unix'],
    quotes: [
      'error',
      'single',
      {
        allowTemplateLiterals: true
      }
    ],
    semi: ['error', 'never'],
    'no-console': 'off',
    'no-unused-vars': [
      'error',
      {
        varsIgnorePattern: '_',
        argsIgnorePattern: '^_'
      }
    ],
    'arrow-parens': ['error', 'always'],
    'no-trailing-spaces': 2,
    'keyword-spacing': 2,
    'space-before-blocks': ['error', 'always'],
    'arrow-spacing': [
      'error',
      {
        before: true,
        after: true
      }
    ],
    'no-else-return': [
      'error',
      {
        allowElseIf: false
      }
    ],
    'no-multiple-empty-lines': [
      'error',
      {
        max: 1
      }
    ]
  }
}
