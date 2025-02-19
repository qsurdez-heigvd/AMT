import http from 'node:http'
import {createWriteStream} from 'node:fs'
import {mkdir} from 'node:fs/promises'

function download(url, dest) {
  return new Promise((resolve) => {
    const file = createWriteStream(dest)
    http.get(url, (response) => {
      response.pipe(file)
    })

    file.on('finish', () => {
      file.close()
      resolve()
    })
  })
}

await mkdir('openapi', { recursive: true, mode: 0o755 })

console.log('Fetching API docs from local API...')
await download('http://localhost:13000/api/api-docs', 'openapi/api-docs.json')
