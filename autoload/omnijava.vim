function! omnijava#setup()
  let s:root_path = filter(split(&rtp, ','), 'v:val =~# "omnijava"')[0]
  let g:omnijava#root_path = s:root_path

  let l:cache_path = s:root_path . '/cache'
  if !isdirectory(l:cache_path)
    call mkdir(l:cache_path)
  endif

  let s:package_list_cache_path = l:cache_path . '/package_list'
  if !filereadable(l:package_list_cache_path)
    let l:result = s:reflection('packages')
    call writefile(l:result, s:package_list_cache_path)
  endif
endfunction

function! omnijava#omnifunc(findstart, base)
  let l:col = col('.')
  let l:line = getline('.')[0 : l:col - 1]
  if a:findstart
    if l:col == 1
      return -1
    else
      return omnijava#get_keyword_col(l:line)
    endif
  else
    call omnijava#setup()
    return omnijava#get_complete_words(l:line, a:base)
  endif
endfunction

function! omnijava#get_keyword_col(text)
  if a:text =~# '^\(import\|package\)'
    " Return the initial column of package name
    return matchend(a:text, '^\(import\|package\)\s\+')
  else
    " TODO: Return the initial column of keyword
    return -1
  endif
endfunction

function! omnijava#get_complete_words(line, keyword)
  let l:list = []
  call add(l:list, s:item_format(a:keyword . '*'))

  if a:line =~# '^\(import\|package\)'
    let l:package_list = s:get_package_list(a:keyword)
    for l:package in l:package_list
      call add(l:list, s:item_format(l:package))
    endfor
  endif

  return l:list
endfunction

function! s:get_package_list(keyword)
  if !exists('s:package_list_cache')
    let s:package_list_cache = {}
  endif

  if !has_key(s:package_list_cache, a:keyword)
    let l:package_list = readfile(s:package_list_cache_path)
    call filter(l:package_list, 'v:val =~# "' . a:keyword . '"')
    let s:package_list_cache[a:keyword] = l:package_list
  endif

  return s:package_list_cache[a:keyword]
endfunction

function! s:reflection(cmd)
  let l:classpath = fnamemodify(globpath(&rtp, 'autoload/omnijava.vim'), ':p:h')
  let l:result = system('java -classpath ' . l:classpath . ' Reflection ' . a:cmd)
  return split(l:result, '\n')
endfunction

function! s:item_format(word)
  return {'word': a:word, 'menu': '[omnijava] ' . a:word}
endfunction

function! omnijava#debug()
  return s:package_list_cache
endfunction
