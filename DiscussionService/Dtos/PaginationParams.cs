namespace DiscussionService.Dtos
{
    public class PaginationParams
    {
        private const int MAX_ITEMS_PER_PAGE = 50;
        private int itemsPerPage;

        public int Page { get; set; } = 1;
        public int ItemsPerPage
        {
            get => itemsPerPage;
            set => itemsPerPage = value > MAX_ITEMS_PER_PAGE ? MAX_ITEMS_PER_PAGE : value;
        }

    }
}