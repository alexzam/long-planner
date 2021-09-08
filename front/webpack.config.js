const path = require('path');
const sveltePreprocess = require('svelte-preprocess');
const MiniCssExtractPlugin = require('mini-css-extract-plugin');
const ProvidePlugin = require('webpack/lib/ProvidePlugin');

const mode = process.env.NODE_ENV || 'development';
const prod = mode === 'production';

module.exports = {
    entry: {
        'bundle': ['./src/index.js']
    },
    output: {
        filename: 'js/[name].js',
        chunkFilename: 'js/[name].[id].js'
    },
    resolve: {
        alias: {
            svelte: path.dirname(require.resolve('svelte/package.json'))
        },
        extensions: ['.mjs', '.js', '.ts', '.svelte'],
        mainFields: ['svelte', 'browser', 'module', 'main']
    },
    module: {
        rules: [
            {
                test: /\.ts$/,
                loader: 'ts-loader',
                exclude: /node_modules/
            },
            {
                test: /\.svelte$/,
                use: {
                    loader: 'svelte-loader',
                    options: {
                        compilerOptions: {
                            dev: !prod
                        },
                        emitCss: true,
                        hotReload: !prod,
                        preprocess: sveltePreprocess({sourceMap: !prod})
                    }
                }
            },
            {
                test: /\.css$/,
                use: [
                    MiniCssExtractPlugin.loader,
                    'css-loader'
                ]
            },
            {
                // required to prevent errors from Svelte on Webpack 5+
                test: /node_modules\/svelte\/.*\.mjs$/,
                resolve: {
                    fullySpecified: false
                }
            },
            {
                test: /\.(woff2?|svg|eot|ttf)$/i,
                type: 'asset/resource',
                generator: {
                    filename: 'fonts/[hash][ext][query]'
                }
            }
        ]
    },
    mode,
    plugins: [
        new MiniCssExtractPlugin({
            filename: 'css/[name].css'
        }),
        new ProvidePlugin({
            $: "jquery",
            jQuery: "jquery"
        })
    ],
    devtool: prod ? false : 'source-map',
    devServer: {
        hot: true
    }
}