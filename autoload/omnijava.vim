function! omnijava#omnifunc(findstart, base)
  let l:col = col('.')
  let l:line = getline('.')[0 : l:col - 1]
  if a:findstart
    if l:col == 1
      return -1
    else
      return s:get_keyword_col(l:line)
    endif
  else
    return s:get_complete_words(l:line, a:base)
  endif
endfunction

function! s:get_keyword_col(text)
  if a:text =~# '^\(import\|package\)'
    " Return the initial column of package name
    return matchend(a:text, '^\(import\|package\)\s\+')
  else
    " TODO: Return the initial column of keyword
    return -1
  endif
endfunction

function! s:get_complete_words(line, keyword)
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
  if !exists('s:packages_cache')
    let s:packages_cache = {}
  endif
  if !has_key(s:packages_cache, a:keyword)
    let l:result = s:reflection('packages ' . a:keyword)
    call filter(l:result, 'v:val =~# "' . a:keyword . '"')
    let s:packages_cache[a:keyword] = l:result
  endif
  return s:packages_cache[a:keyword]
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
  return s:packages_cache
endfunction
