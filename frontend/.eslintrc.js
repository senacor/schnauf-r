module.exports = {
    parser: "babel-eslint",
    env: {
        browser: true,
        es6: true,
        'jest/globals': true
    },
    parserOptions: {
        ecmaVersion: 6,
        sourceType: "module",
        ecmaFeatures: {
            jsx: true,
            classes: true
        }
    },
    settings: {
        react: {
            pragma: "React",  // Pragma to use, default to "React"
            version: "detect", // React version. "detect" automatically picks the version you have installed.
        }
    },
    plugins: ['jest', 'react'],
    extends: ['eslint:recommended', 'plugin:jest/recommended', 'plugin:react/recommended'],
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