import { Fragment } from 'react';

interface PaginationProps {
  page: number;
  size: number;
  totalPages: number;
  totalElements: number;
  onChange: (page: number) => void;
}

export function Pagination({ page, size, totalPages, totalElements, onChange }: PaginationProps) {
  const lastPage = Math.max(1, totalPages || 1);
  const pages = Array.from({ length: lastPage }, (_, index) => index).filter(
    (index) => Math.abs(index - page) <= 2 || index === 0 || index === lastPage - 1,
  );

  return (
    <div className="pagination">
      <span className="muted">총 {totalElements}건 · {page + 1}/{lastPage} 페이지 · {size}건씩</span>
      <div className="pagination-buttons">
        <button type="button" className="secondary-button" disabled={page <= 0} onClick={() => onChange(page - 1)}>이전</button>
        {pages.map((index, idx) => (
          <Fragment key={index}>
            {idx > 0 && pages[idx - 1] !== index - 1 ? <span className="pagination-gap">…</span> : null}
            <button
              type="button"
              className={index === page ? 'primary-button small' : 'secondary-button small'}
              onClick={() => onChange(index)}
            >
              {index + 1}
            </button>
          </Fragment>
        ))}
        <button type="button" className="secondary-button" disabled={page >= lastPage - 1} onClick={() => onChange(page + 1)}>다음</button>
      </div>
    </div>
  );
}
