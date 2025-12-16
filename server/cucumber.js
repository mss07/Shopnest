export default {
  default: {
    require: ['features/step_definitions/**/*.js'],
    requireModule: ['@babel/register'],
    require_module: ['features/support/hooks.js'],
    format: [
      'progress-bar',
      'html:cucumber-report.html',
      'json:cucumber-report.json'
    ],
    formatOptions: {
      snippetInterface: 'async-await'
    }
  }
};
