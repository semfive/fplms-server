namespace DiscussionService.Dtos
{
    public class PaginationParams
    {
        private const int MAX_ITEMS_PER_PAGE = 50;
        private int pageSize;

        public int PageNumber { get; set; } = 1;
        public int PageSize
        {
            get => pageSize;
            set => pageSize = value > MAX_ITEMS_PER_PAGE ? MAX_ITEMS_PER_PAGE : value;
        }

    }
}